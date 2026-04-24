package com.cug.config;


import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig{
    public static final String EXCHANGE_NAME = "doctor.exchange.direct";
    public static final String RELEASE_QUEUE_NAME = "doctor.queue.release";
    public static final String RELEASE_ROUTING_KEY = "doctor.release";
    public static final String APPOINT_QUEUE_NAME="user.queue.appoint";
    public static final String APPOINT_ROUTING_KEY="user.appoint";
    @Bean
    public Exchange exchange(){
        return new DirectExchange(EXCHANGE_NAME,true,false);
    }
    @Bean
    public Queue releaseQueue(){
        return new Queue(RELEASE_QUEUE_NAME,true,false,false);
    }
    @Bean
    public Queue appointQueue(){
        return new Queue(APPOINT_QUEUE_NAME,true,false,false);
    }
    @Bean
    public Binding releaseBinding() {
        return BindingBuilder
                .bind(releaseQueue())
                .to(exchange())
                .with(RELEASE_ROUTING_KEY).noargs();
    }
    @Bean
    public Binding appointBinding() {
        return BindingBuilder
                .bind(appointQueue())
                .to(exchange())
                .with(APPOINT_ROUTING_KEY).noargs();
    }
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

}
