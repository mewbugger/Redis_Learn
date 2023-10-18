package com.wly.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        //这一行设置了RedisTemplate用于序列化键的序列化器
        //RedisSerializer.string()表示使用字符串序列化器，以便可以将键视为字符串。
        //RedisTemplate默认的序列化器是jdk的，redis内读取会乱码
        redisTemplate.setKeySerializer(RedisSerializer.string());
        //如果value也序列化成string，则当value是一个实体类的对象的时候，会报错
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //value hashmap序列化
        redisTemplate.setHashValueSerializer(RedisSerializer.string());
        //key hashmap序列化
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        return redisTemplate;
    }
}
