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
        log.info("메서드 이름: receiverConsumer()");
        log.info("title: {}, message:{}",message.getTitle(),message.getMessage());
    }

    @RabbitListener(queues = "hello.queue")
    public void receiverConsumer2(MessageDto message){
        log.info("메서드 이름: receiverConsumer()2");
        log.info("direct exchange는 하나의 큐로만 가게 되어있네 이건 안타네.");
        log.info("title: {}, message:{}",message.getTitle(),message.getMessage());
    }

    /**
     * 토픽 큐는 둘 receiverSns,receiverDtw 메서드 둘중 한곳에서
     * 레이스를 펼친 후 가져간다.
     */
    @RabbitListener(queues = "hello.sign.dtw")
    public void receiverDtw(MessageDto message){
        log.info("title: {}, message:{}",message.getTitle(),message.getMessage());
        log.info("메서드 이름: receiverDtw()");
    }

    @RabbitListener(queues = "hello.sign.dtw")
    public void receiverSns(MessageDto message){
        log.info("title: {}, message:{}",message.getTitle(),message.getMessage());
        log.info("메서드 이름: receiverSns()");
    }
}
