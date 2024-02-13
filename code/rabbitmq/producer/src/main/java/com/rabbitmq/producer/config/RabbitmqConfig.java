package com.rabbitmq.producer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.port}")
    private int port;

    // queue 이름
    @Bean
    Queue queue() {
        return new Queue("hello.queue",false);
    }


    @Bean
    Queue signDtwQueue(){
        return new Queue("hello.sign.dtw");
    }

//    @Bean
//    Queue signSnsQueue(){
//        return new Queue("hello.sign.sns");
//    }


    // topic exchange 설정
    @Bean
    TopicExchange signTopicExchange(){
        return new TopicExchange("hello.sign");
    }

    @Bean
    Binding signDtwTopicBinding(TopicExchange signTopicExchange,@Qualifier("signDtwQueue") Queue queue) {
        Binding with = BindingBuilder.bind(queue).to(signTopicExchange).with("hello.sign.#");
        System.out.println(with);
        return with;
    }

//    @Bean
//    Binding signSnsTopicBinding(TopicExchange signTopicExchange,@Qualifier("signSnsQueue") Queue queue) {
//        Binding with = BindingBuilder.bind(queue).to(signTopicExchange).with("hello.sign.#");
//        System.out.println(with);
//        return with;
//    }

    // exchange 이름
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange("hello.exchange");
    }

    // 바인딩 조건 exchange to queue
    @Bean
    Binding binding(DirectExchange directExchange, Queue queue) {
        return BindingBuilder.bind(queue).to(directExchange).with("hello.key");
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
