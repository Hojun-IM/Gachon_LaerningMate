package com.gachon.learningmate.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 만료시간 포함한 값 저장
    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    // 주어진 키에 대한 값을 검색
    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if(values.get(key) != null) {
            return (String) values.get(key);
        }
        return null;
    }

    // 데이터 삭제
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    // 주어진 키에 대한 만료시간 설정
    public void expireValues(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }

    // 조회하려는 데이터 확인
    public boolean existsValues(String key) {
        return redisTemplate.hasKey(key);
    }
}
