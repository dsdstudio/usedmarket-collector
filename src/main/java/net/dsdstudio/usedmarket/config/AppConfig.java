package net.dsdstudio.usedmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * usedmarket-collector net.dsdstudio.usedmarket.config
 *
 * @author : bhkim
 * @since : 2015-01-24 오전 1:12
 */
@Configuration
@EnableScheduling
@PropertySource("classpath:/config.properties")
public class AppConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory f = new JedisConnectionFactory();
        f.setPort(17300);
        return f;
    }
    @Bean
    public StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
        System.out.println(connectionFactory);
        return new StringRedisTemplate(connectionFactory);
    }
}