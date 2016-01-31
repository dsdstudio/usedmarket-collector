package net.dsdstudio.usedmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}