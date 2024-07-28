package com.ussd.repository.impl;

import com.ussd.repository.RedisRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;
@Repository
public class RedisRepositoryImpl implements RedisRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    public void init() {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        // set expire on the key
        redisTemplate.expire(
                key,
                86400,
                TimeUnit.SECONDS
        );
    }

    @Override
    public void clearValue(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void saveHashField(String key, String fieldKey, String value) {
        hashOperations.put(key, fieldKey, value);
        redisTemplate.expire(
                key,
                86400,
                TimeUnit.SECONDS
        );
    }

    @Override
    public String getHashField(String key, String fieldKey) {
        return hashOperations.get(key, fieldKey);
    }

    @Override
    public void saveHashValues(String key, Map<String, String> valuesMap) {
        valuesMap.forEach((hashKey, value) -> {
            hashOperations.put(key, hashKey, value);
        });
        // set expire on the key
        redisTemplate.expire(
                key,
                86400,
                TimeUnit.SECONDS
        );
    }

    @Override
    public Map<String, String> getHashValues(String key) {
        return hashOperations.entries(key);
    }

    @Override
    public void deleteHashField(String key, String fieldKey) {
        try {
            hashOperations.delete(key, fieldKey);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void deleteHash(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
