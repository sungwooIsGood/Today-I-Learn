package com.example.RedisCaching.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ExternalApiService {

    /**
     * 외부 서비스나 DB 호출역할을 하는 메서드
     */
    public String getUserName(String userId){

        //  0.5초의 성능 저하가 있는 상태
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Getting user name from other service..");

        // user 2명만 존재한다고 가정
        if(userId.equals("A")){
            return "adam";
        } else{
            return "bob";
        }
    }

    @Cacheable(cacheNames = "userAgeCache", key = "#userId") // String userId 값을 쓴다고 지정한 것(변수 사용)
    public int getUserAge(String userId){

        //  0.5초의 성능 저하가 있는 상태
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Getting user age from other service..");

        // user 2명만 존재한다고 가정
        if(userId.equals("A")){
            return 28;
        } else{
            return 32;
        }
    }
}
