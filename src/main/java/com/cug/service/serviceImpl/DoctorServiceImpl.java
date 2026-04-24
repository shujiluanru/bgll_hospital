package com.cug.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.json.JSONUtil;
import com.cug.config.MQConfig;
import com.cug.constant.CacheConstant;
import com.cug.context.DoctorContext;
import com.cug.domain.pojo.Doctor;
import com.cug.domain.pojo.R;
import com.cug.domain.pojo.ReleaseLog;
import com.cug.domain.pojo.ReleaseMessage;
import com.cug.domain.vo.ReleaseLogVO;
import com.cug.exception.ServerException;
import com.cug.mapper.DoctorMapper;
import com.cug.service.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorMapper doctorMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    public DoctorServiceImpl(DoctorMapper doctorMapper,StringRedisTemplate stringRedisTemplate,RabbitTemplate rabbitTemplate) {
        this.doctorMapper = doctorMapper;
        this.stringRedisTemplate=stringRedisTemplate;
        this.rabbitTemplate=rabbitTemplate;
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
        }
        //缓存命中
        return JSONUtil.toList(JSONUtil.parseArray(doctors), Doctor.class);
    }
}
