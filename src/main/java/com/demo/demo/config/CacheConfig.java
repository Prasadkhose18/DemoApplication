package com.demo.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users", "accounts", "balance");
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean("userEmailKeyGenerator")
    public KeyGenerator userEmailKeyGenerator() {
        return (target, method, params) -> {
            try {
                var currentUserService = target.getClass()
                        .getDeclaredField("currentUserService");
                currentUserService.setAccessible(true);
                var service = currentUserService.get(target);
                var getCurrentUserMethod = service.getClass()
                        .getMethod("getCurrentUser");
                var user = getCurrentUserMethod.invoke(service);
                var getEmailMethod = user.getClass().getMethod("getEmail");
                return getEmailMethod.invoke(user);
            } catch (Exception e) {
                throw new RuntimeException("Unable to generate cache key", e);
            }
        };
    }
}
