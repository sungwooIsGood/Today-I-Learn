package com.rabbitmq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReceiverService {

    @RabbitListener(queues = "hello.queue")
    public void receiverConsumer(MessageDto message){
        log.info("title: {}, message:{}",message.getTitle(),message.getMessage());
    }
}
