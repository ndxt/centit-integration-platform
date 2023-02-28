package com.centit.framework.tenant.config;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.support.spring.data.redis.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, JSONObject> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, JSONObject> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        GenericFastJsonRedisSerializer serializer = new GenericFastJsonRedisSerializer();
        template.setValueSerializer(serializer);
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

}
