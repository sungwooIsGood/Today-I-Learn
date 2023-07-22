package com.rabbitmq.producer.service;

import com.rabbitmq.producer.data.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqProducerService {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(MessageDto message){

        /**
         * 자바 객체를 amqp 메세지로 변환 후 exchange로 전달된다.
         */
        rabbitTemplate.convertAndSend("hello.exchange","hello.key",message);
        log.info("메세지 큐로 전송이 성공적입니다.");
    }
}
