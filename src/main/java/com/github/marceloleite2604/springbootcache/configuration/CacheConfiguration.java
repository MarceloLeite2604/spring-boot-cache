package com.github.marceloleite2604.springbootcache.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
public class CacheConfiguration {

  @Bean
  public RedisCacheManagerBuilderCustomizer createRedisCacheManagerBuilderCustomizer(RedisSerializer<Object> redisSerializer) {
    return builder ->
        builder
            .withCacheConfiguration("message",
                RedisCacheConfiguration.defaultCacheConfig()
                    .disableCachingNullValues()
                    .entryTtl(Duration.ofSeconds(60))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer)));

  }

  @Bean
  public RedisSerializer<Object> createRedisSerializer(ObjectMapper objectMapper) {
    var modifiedObjectMapper = objectMapper.copy();

    modifiedObjectMapper = modifiedObjectMapper.activateDefaultTyping(
        objectMapper.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY);

    modifiedObjectMapper.registerModule(new JavaTimeModule());
    return new GenericJackson2JsonRedisSerializer(modifiedObjectMapper);
  }
}
