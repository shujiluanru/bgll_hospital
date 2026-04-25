package com.cug.config;

import com.cug.constant.CacheConstant;
import com.cug.domain.pojo.Reservation;
import com.cug.domain.pojo.ReservationResourceData;
import com.cug.exception.ServerException;
import com.cug.mapper.DoctorMapper;
import com.cug.mapper.ReservationMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CacheWarmUp {
    private final StringRedisTemplate stringRedisTemplate;
    private final DoctorMapper doctorMapper;
    private final ReservationMapper reservationMapper;
    public CacheWarmUp(StringRedisTemplate stringRedisTemplate,DoctorMapper doctorMapper,ReservationMapper reservationMapper)
    {
        this.stringRedisTemplate=stringRedisTemplate;
        this.doctorMapper=doctorMapper;
        this.reservationMapper=reservationMapper;
    }
    @PostConstruct
    public void cacheWarm()
    {
        log.info("医生号源缓存开始预热");
        List<ReservationResourceData> resource = doctorMapper.getResourceNum();
        if (resource == null || resource.isEmpty()) {
            log.warn("没有查询到医生号源数据");
            throw new ServerException("未查询到号源");
        }
        Map<String, String> hashMap = resource.stream()
                .collect(Collectors.toMap(
                        item -> String.valueOf(item.getDoctorId()),
                        item -> String.valueOf(item.getReservationResource())
                ));

        // 存入 Redis Hash
        stringRedisTemplate.opsForHash().putAll(
                CacheConstant.DOCTOR_RESERVATION_RESOURCE,
                hashMap
        );
        log.info("预热完成,共{}条数据",hashMap.size());//TODO新增医生时的处理，到时新增接口直接重新调用Mapper预热
        log.info("预约数据开始预热");
        List<Reservation> reservations = reservationMapper.getReservations();
        if(reservations.isEmpty())
        {
            log.info("无预约数据需要预热");
        }
        Map<String, String> reservationMap = reservations.stream()
                .collect(Collectors.toMap(
                        e -> e.getUserId().toString() + ":" + e.getDoctorId().toString(),
                        e -> "1",
                        (v1, v2) -> v1  // 如果有重复，保留第一个
                ));

        stringRedisTemplate.opsForHash().putAll(CacheConstant.RESERVATION_LIST, reservationMap);
        log.info("预约数据缓存预热完毕，共{}条数据", reservations.size());
    }
    //定义一个定时任务
    @Scheduled(cron="0 0 0 * * ?")
    public void WarmUp()
    {
        this.cacheWarm();
    }

}
