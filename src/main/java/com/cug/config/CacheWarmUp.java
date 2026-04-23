package com.cug.config;

import com.cug.constant.CacheConstant;
import com.cug.domain.pojo.ReservationResourceData;
import com.cug.exception.ServerException;
import com.cug.mapper.DoctorMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    public CacheWarmUp(StringRedisTemplate stringRedisTemplate,DoctorMapper doctorMapper)
    {
        this.stringRedisTemplate=stringRedisTemplate;
        this.doctorMapper=doctorMapper;
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
    }


}
