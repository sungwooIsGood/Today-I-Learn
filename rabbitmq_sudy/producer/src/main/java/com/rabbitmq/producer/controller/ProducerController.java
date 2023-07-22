package com.rabbitmq.producer.controller;

import com.rabbitmq.producer.data.dto.MessageDto;
import com.rabbitmq.producer.service.RabbitMqProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProducerController {

    private final RabbitMqProducerService producer;

    @PostMapping("/send")
    public void sendMessage(@RequestBody MessageDto message){
        producer.sendMessage(message);
    }
}
