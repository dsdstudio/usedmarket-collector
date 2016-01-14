package net.dsdstudio.usedmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * usedmarket-collector
 * net.dsdstudio.usedmarket
 *
 * @author : bhkim
 * @since : 2015. 1. 9..
 */

@SpringBootApplication
@ComponentScan(basePackages = "net.dsdstudio.usedmarket")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
