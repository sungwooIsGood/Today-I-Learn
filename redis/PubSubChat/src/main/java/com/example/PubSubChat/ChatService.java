package com.example.PubSubChat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class ChatService implements MessageListener {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    private RedisMessageListenerContainer container;


    public void enterCharRoom(String chatRoomName){

        container.addMessageListener(this,new ChannelTopic(chatRoomName)); // MessageListener를 구현한 onMessage이다.

        Scanner in = new Scanner(System.in);

        while (in.hasNextLine()){
            String line = in.nextLine();
            if(line.equals("q")){
                System.out.println("Quit..");
                break;
            }
            // 입력된 라인을 topic이름을 적어주고 line을 보내주면 listener들로 전송하게 된다.
            redisTemplate.convertAndSend(chatRoomName, line);
        }

        container.removeMessageListener(this); // message 제거
    }


    // 메시지 listener를 onMessage로 상속받아 구현함으로써
    // redis subscriber를 통해 도착한 메세지를 확인 할 수 있다.
    @Override
    public void onMessage(Message message, byte[] pattern){
        System.out.println("Message: " + message.toString());
    }

}
