package com.example.RedisCaching.service;

import com.example.RedisCaching.dto.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private ExternalApiService externalApiService;

    // 추가
    @Autowired
    RedisTemplate<String,String>redisTemplate; //<String, String> => String type

    public UserProfile getUserProfile(String userId){

        String userName = null;

        // String 연산자 템플릿
        ValueOperations<String,String> ops = redisTemplate.opsForValue(); // opsForValue() => redis에서 String type
        String cachedName = ops.get("nameKey:" + userId);// redis ex) get nameKey A


        // Cache Aside 패턴 적용
        if(cachedName != null){
            userName = cachedName; // cache에 있으면 cache 데이터를 이용
        } else{
            userName = externalApiService.getUserName(userId);
            ops.set("nameKey:" + userId, userName, 300, TimeUnit.SECONDS); // cache에 없으면 cache에 저장하고 5초 동안 적용 마지막 TimeUnit은 단위
        }


        /**
         * 외부 API 혹은 외부 DB의 역할을 대신 하는 역할
         * userName과 age를 받아오려면 총 1초가 걸린다.
         */
        int userAge = externalApiService.getUserAge(userId);

        return new UserProfile(userName,userAge);
    }
}
