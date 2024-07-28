package com.ussd.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@Lazy
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${redis.hostname}")
    private String redisHostName;
    @Value("${redis.port}")
    private int redisPort;
    @Value("${redis.auth-token}")
    private String redisPassword;
    @Value("${redis.database}")
    private String redisDatabase;
    @Value("${redis.ssl:true}")
    private Boolean ssl;
    @Value("${redis.command-timeout:5}")
    private Long commandTimeout;

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory() {
        var clientConfigBuilder = LettuceClientConfiguration.builder();

        if (this.ssl) {
            clientConfigBuilder = clientConfigBuilder.useSsl().and();
        }
        var clientConfig = clientConfigBuilder.commandTimeout(Duration.ofSeconds(commandTimeout))
                .shutdownTimeout(Duration.ZERO)
                .build();
        var redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHostName, redisPort);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPassword));
        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
    }

    @Bean("redisTemplate")
    public static RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Primary
    @Bean("cacheManager") // Default cache manager is infinite
    public static CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().prefixCacheNameWith("das"))
                .build();
    }

    @Bean("cacheManager24Hour")
    public static RedisCacheManager cacheManager24Hour(RedisConnectionFactory redisConnectionFactory) {
        Duration expiration = Duration.ofHours(24);
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig()
                                .prefixCacheNameWith("das").entryTtl(expiration)
                ).build();
    }

    @Bean("cacheManager48Hour")
    public static RedisCacheManager cacheManager48Hour(RedisConnectionFactory redisConnectionFactory) {
        Duration expiration = Duration.ofHours(48);
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig()
                                .prefixCacheNameWith("das").entryTtl(expiration)
                ).build();
    }
}