package com.example.RedisCaching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory){
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues() // null 금지
                .entryTtl(Duration.ofSeconds(10)) // 기본 TTL(유효시간)
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                );

        HashMap<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("userAgeCache",RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(5))); // 특정 캐시에 대한 유효시간 설정, custom 설정

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)
                .cacheDefaults(configuration)
                .withInitialCacheConfigurations(configMap)
                .build();
    }
}
