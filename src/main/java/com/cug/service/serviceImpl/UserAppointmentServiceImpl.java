package com.cug.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.cug.config.MQConfig;
import com.cug.constant.CacheConstant;
import com.cug.context.UserContext;
import com.cug.domain.pojo.Doctor;
import com.cug.domain.pojo.R;
import com.cug.domain.pojo.Reservation;
import com.cug.domain.vo.DoctorInfoVO;
import com.cug.domain.vo.DoctorResourceVO;
import com.cug.service.DoctorService;
import com.cug.service.UserAppointmentService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserAppointmentServiceImpl implements UserAppointmentService {
    private final DoctorService doctorService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    public UserAppointmentServiceImpl(DoctorService doctorService,StringRedisTemplate stringRedisTemplate,RabbitTemplate rabbitTemplate) {
        this.doctorService = doctorService;
        this.stringRedisTemplate=stringRedisTemplate;
        this.rabbitTemplate=rabbitTemplate;
    }

    @Override
    public R getDoctorList() {
        List<Doctor> doctorList = doctorService.getDoctorList();
        List<DoctorInfoVO> doctorInfoVOList = BeanUtil.copyToList(doctorList, DoctorInfoVO.class);
        return R.ok(doctorInfoVOList);
    }

    @Override
    public R getResources() {
        List<DoctorResourceVO>resources=doctorService.getResources();
        return R.ok(resources);
    }

    @Override
    public R grab(Long doctorId) {
        //获取用户id
        Long userId= UserContext.getUserId();
        //使用lua脚本执行原子性抢号
        ClassPathResource lua=new ClassPathResource("lua/userGrabResource.lua");
        String luaStr=lua.readUtf8Str();
        RedisScript<Long> redisScript= RedisScript.of(luaStr,Long.class);
        Long v = stringRedisTemplate.execute(redisScript, List.of(CacheConstant.DOCTOR_RESERVATION_RESOURCE,CacheConstant.RESERVATION_LIST), doctorId.toString(),userId.toString());
        if(v==-1)
            return R.error("预约失败,该医生号源已空，请等待放号");
        if(v==-2)
            return R.error("预约失败,该医生已经预约过了!");
        //预约成功,组装预约实体
        Reservation reservation=new Reservation();
        //基于redis生成全局唯一ID
        reservation.setId(this.generateOrderNo());
        reservation.setDoctorId(doctorId);
        reservation.setUserId(userId);
        reservation.setCreateTime(LocalDateTime.now());
        //发送预约体通知数据库
        rabbitTemplate.convertAndSend(MQConfig.EXCHANGE_NAME,MQConfig.APPOINT_ROUTING_KEY,reservation);
        return R.ok(reservation.getId());
    }

    public String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String key = CacheConstant.RESERVATION_ID_INCREMENT_SEGMENT + date;
        long expireSeconds = 80L;//80秒后改1分钟segment失效
        // 2. 使用 Lua 脚本保证原子性
        String luaScript =
                "local current = redis.call('GET', KEYS[1]) " +
                        "if current then " +
                        "   return redis.call('INCR', KEYS[1]) " +
                        "else " +
                        "   redis.call('SETEX', KEYS[1], ARGV[1], 1) " +
                        "   return 1 " +
                        "end";

        Long seq = stringRedisTemplate.execute(
                new DefaultRedisScript<>(luaScript, Long.class),
                Collections.singletonList(key),
                String.valueOf(expireSeconds)
        );
        return date + String.format("%010d", seq);
    }
}
