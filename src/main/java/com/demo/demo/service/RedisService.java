package com.demo.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {


    private final RedisTemplate<String,Object> redisTemplate;


    public void save(
            String key,
            Object value,
            long timeout
    ){

        redisTemplate.opsForValue()
                .set(
                        key,
                        value,
                        Duration.ofMinutes(timeout)
                );
    }


    public Object get(String key){

        return redisTemplate.opsForValue()
                .get(key);
    }


    public void delete(String key){

        redisTemplate.delete(key);
    }

}