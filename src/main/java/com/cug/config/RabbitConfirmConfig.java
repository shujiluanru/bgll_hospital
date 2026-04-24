package com.cug.config;

import com.cug.exception.ServerException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitConfirmConfig {
    private final RabbitTemplate rabbitTemplate;
    public RabbitConfirmConfig(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;

    }
    @PostConstruct
    public void init() {
        // 设置确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息发送确认成功: correlationId={}",
                        correlationData != null ? correlationData.getId() : null);
            } else {
                log.error("消息发送确认失败: correlationId={}, cause={}",
                        correlationData != null ? correlationData.getId() : null, cause);
                throw new ServerException("消息发送失败");
            }
        });

        // 设置消息返回回调（当消息无法路由到队列时触发）
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("消息路由失败: exchange={}, routingKey={}, replyCode={}, replyText={}, message={}",
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getMessage());
            throw new ServerException("消息发送失败");
        });

        // 开启 mandatory 模式
        rabbitTemplate.setMandatory(true);
    }

}
