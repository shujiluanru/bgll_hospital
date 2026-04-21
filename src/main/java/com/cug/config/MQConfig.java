package com.cug.config;


import com.rabbitmq.client.AMQP;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;

public class MQConfig{
    @Bean
    public MessageConverter messageConverter(){
        return new SimpleMessageConverter();
    }
}
