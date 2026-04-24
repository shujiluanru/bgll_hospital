package com.cug.service.serviceImpl;

import com.cug.config.MQConfig;
import com.cug.domain.pojo.ReleaseMessage;
import com.cug.exception.ServerException;
import com.cug.mapper.DoctorMapper;
import com.cug.service.ResourceRabbitMQService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceRabbitMQServiceImpl implements ResourceRabbitMQService {
    private final DoctorMapper doctorMapper;
    private final StringRedisTemplate stringRedisTemplate;
    public ResourceRabbitMQServiceImpl(DoctorMapper doctorMapper,StringRedisTemplate stringRedisTemplate){
        this.doctorMapper = doctorMapper;
        this.stringRedisTemplate=stringRedisTemplate;
    }
    @Override
    @RabbitListener(queues = MQConfig.RELEASE_QUEUE_NAME)
    @Transactional(rollbackFor = ServerException.class)
    public void release(ReleaseMessage releaseMessage) {
        //确保幂等性，取出messageId
        String messageId = releaseMessage.getMessageId();
        if(checkIdempotent(messageId))
            return;
        //取出医生id，放号数量
        Long doctorId = releaseMessage.getDoctorId();
        int releaseNum = releaseMessage.getReleaseNum();
        try {

            doctorMapper.release(doctorId, releaseNum);
            //记录放号日志
            doctorMapper.addReleaseLog(releaseMessage);
        } catch (Exception e) {
            throw new ServerException("放号失败");
        }
    }
    private boolean checkIdempotent(String messageId){
        //判断messageId是否已经处理过
        String flag = stringRedisTemplate.opsForValue().get(messageId);
        if(flag != null){
            return true;
        }
        //设置messageId已经处理过
        stringRedisTemplate.opsForValue().set(messageId,"1");
        return false;
    }
}
