package com.cug.service.serviceImpl;

import com.cug.config.MQConfig;
import com.cug.domain.pojo.ReleaseMessage;
import com.cug.domain.pojo.Reservation;
import com.cug.exception.ServerException;
import com.cug.mapper.DoctorMapper;
import com.cug.service.ResourceRabbitMQService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class ResourceRabbitMQServiceImpl implements ResourceRabbitMQService {
    private final DoctorMapper doctorMapper;
    private final StringRedisTemplate stringRedisTemplate;
    public ResourceRabbitMQServiceImpl(DoctorMapper doctorMapper,StringRedisTemplate stringRedisTemplate){
        this.doctorMapper = doctorMapper;
        this.stringRedisTemplate=stringRedisTemplate;
    }
    @RabbitListener(queues = MQConfig.RELEASE_QUEUE_NAME)
    @Transactional(rollbackFor = Exception.class)
    public void release(ReleaseMessage releaseMessage,Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        //确保幂等性，取出messageId
        String messageId = releaseMessage.getMessageId();
        if(checkIdempotent(messageId,"release:")) {
            channel.basicAck(deliveryTag, false);
            return;
        }
        //取出医生id，放号数量
        Long doctorId = releaseMessage.getDoctorId();
        int releaseNum = releaseMessage.getReleaseNum();
        try {

            doctorMapper.release(doctorId, releaseNum);
            //记录放号日志
            doctorMapper.addReleaseLog(releaseMessage);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
            throw new ServerException("放号失败");
        }
    }
    @RabbitListener(queues= MQConfig.APPOINT_QUEUE_NAME)
    @Transactional(rollbackFor = Exception.class)
    public void appoint(Reservation reservation, Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        //幂等性，确保不会重复消费
        if(checkIdempotent(reservation.getId(),"appoint:")) {
            channel.basicAck(deliveryTag, false);
            return;
        }
        try {
            //扣减号源
            doctorMapper.release(reservation.getDoctorId(), -1);
            //增加预约记录
            doctorMapper.addReservation(reservation);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
            throw new ServerException("预约失败");
        }

    }
    private boolean checkIdempotent(String messageId,String prefix){
        //判断messageId是否已经处理过
        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(prefix + messageId, "1", 10, TimeUnit.SECONDS);
        return !b;
    }

}
