package com.cug.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.json.JSONUtil;
import com.cug.config.MQConfig;
import com.cug.constant.CacheConstant;
import com.cug.context.DoctorContext;
import com.cug.domain.pojo.*;
import com.cug.domain.vo.DoctorResourceVO;
import com.cug.domain.vo.ReleaseLogVO;
import com.cug.domain.vo.ReservationForDoctorVO;
import com.cug.exception.ServerException;
import com.cug.mapper.DoctorMapper;
import com.cug.mapper.ReservationMapper;
import com.cug.service.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorMapper doctorMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final ReservationMapper reservationMapper;

    public DoctorServiceImpl(DoctorMapper doctorMapper, StringRedisTemplate stringRedisTemplate, RabbitTemplate rabbitTemplate, ReservationMapper reservationMapper) {
        this.doctorMapper = doctorMapper;
        this.stringRedisTemplate=stringRedisTemplate;
        this.rabbitTemplate=rabbitTemplate;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public R updateDescription(Map<String, String> data) {
        //取出doctor的id
        Long id = DoctorContext.getDoctorId();
        String description = data.get("description");
        int i = doctorMapper.updateDescription(id, description);
        if (i == 0)
            throw new ServerException("更新失败");
        //使缓存失效
        stringRedisTemplate.delete(CacheConstant.DOCTOR_INFO_LIST);
        return R.ok();
    }

    @Override
    public R getReleaseLog() {
        Long doctorId= DoctorContext.getDoctorId();
        List<ReleaseLog> releaseLog = doctorMapper.getReleaseLog(doctorId);
        if (releaseLog == null)
            throw new ServerException("获取失败");
        log.info(releaseLog.toString());
        List<ReleaseLogVO> releaseLogVO = BeanUtil.copyToList(releaseLog, ReleaseLogVO.class);
        return R.ok(releaseLogVO);
    }
    /**
     * 医生发布号源，添加号源，高并发难点
     *data:
     * param:releaseNum
     */
    @Override
    public R release(Map<String, String> data) {
        //1.取出医生id
        Long doctorId= DoctorContext.getDoctorId();
        //2.取出放号数量
        int releaseNum = Integer.parseInt(data.get("releaseNum"));
        if(releaseNum<=0||releaseNum>20)
            throw new ServerException("放号数量不合规");
        //3.原子性放号至redis
        ClassPathResource classPathResource = new ClassPathResource("lua/doctorReleaseResource.lua");
        String lua = classPathResource.readUtf8Str();
        RedisScript<Long> redisScript = RedisScript.of(lua, Long.class);
        stringRedisTemplate.execute(redisScript, Collections.singletonList(CacheConstant.DOCTOR_RESERVATION_RESOURCE), doctorId.toString(), String.valueOf(releaseNum));
        //4.调用MQ发送消息通知数据库
        //4.1组装消息
        ReleaseMessage releaseMessage = new ReleaseMessage();
        releaseMessage.setDoctorId(doctorId);
        releaseMessage.setReleaseNum(releaseNum);
        releaseMessage.setReleaseTime(LocalDateTime.now());
        releaseMessage.setMessageId(UUID.randomUUID().toString());
        //4.2发送消息
        rabbitTemplate.convertAndSend(MQConfig.EXCHANGE_NAME,MQConfig.RELEASE_ROUTING_KEY,releaseMessage);
        return R.ok();
    }

    @Override
    public List<Doctor> getDoctorList() {
        //查询Redis是否存在医生列表
        String doctors=stringRedisTemplate.opsForValue().get(CacheConstant.DOCTOR_INFO_LIST);
        if(doctors==null || doctors.isBlank()) {
            List<Doctor> doctorList = doctorMapper.getDoctorList();
            //写入缓存
            String jsonStr = JSONUtil.toJsonStr(doctorList);
            stringRedisTemplate.opsForValue().set(CacheConstant.DOCTOR_INFO_LIST,jsonStr);
            stringRedisTemplate.expire(CacheConstant.DOCTOR_INFO_LIST,10, TimeUnit.SECONDS);
            return doctorList;
        }
        //缓存命中
        return JSONUtil.toList(JSONUtil.parseArray(doctors), Doctor.class);
    }

    @Override
    public List<DoctorResourceVO> getResources() {
        //从缓存中获取号源数据封装为List<DoctorResourceVO>
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(CacheConstant.DOCTOR_RESERVATION_RESOURCE);
        if(entries.isEmpty())
        {
            throw new ServerException("没有查询到医生号源数据");
        }
        return entries.entrySet().stream()
                .map(entry -> {
                    DoctorResourceVO doctorResourceVO = new DoctorResourceVO();
                    doctorResourceVO.setDoctorId(Long.parseLong(entry.getKey().toString()));
                    doctorResourceVO.setResourceNum(Integer.parseInt(entry.getValue().toString()));
                    return doctorResourceVO;
                })
                .toList();

    }

    @Override
    public R getReservations() {
        List<ReservationForDoctorVO>list=reservationMapper.getReservationsForDoctor(DoctorContext.getDoctorId());
        return R.ok(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R confirmReservation(Map<String, String> data) {
        String reservationId = data.get("reservation_id");
        //获取Reservation实体
        Reservation reservation = reservationMapper.getReservationById(reservationId);
        String userId = reservation.getUserId().toString();
        String doctorId= reservation.getDoctorId().toString();
        //确认预约,清除redis中的状态
        stringRedisTemplate.opsForHash().delete(CacheConstant.RESERVATION_LIST,userId+":"+doctorId);
        //持久化数据库,设置状态为over
        int i = reservationMapper.confirmReservation(reservationId);
        if(i==0)
            throw new ServerException("确认失败");
        return R.ok();

    }
}
