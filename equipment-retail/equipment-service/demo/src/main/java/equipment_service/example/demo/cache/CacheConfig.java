package equipment_service.example.demo.cache;

import equipment_service.example.demo.db.equipment.EquipmentEntity;
import equipment_service.example.demo.dto.equipment.EquipmentResponseDto;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public GenericJacksonJsonRedisSerializer redisJsonSerializer(){

        var validator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("equipment_service.example.demo")
                .allowIfSubType("java.util.")
                .allowIfSubType(BigDecimal.class)
                .build();
        return GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(validator)
                .build();
    }


    @Bean
    public RedisTemplate<String, EquipmentEntity> redisEquipmentTemplate(
            RedisConnectionFactory redisConnectionFactory,
            GenericJacksonJsonRedisSerializer redisSerializer
    ){
        RedisTemplate<String, EquipmentEntity> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());

        redisTemplate.setValueSerializer(redisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            GenericJacksonJsonRedisSerializer redisSerializer
    ){

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(1))
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(redisSerializer)
                        );
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }

}
