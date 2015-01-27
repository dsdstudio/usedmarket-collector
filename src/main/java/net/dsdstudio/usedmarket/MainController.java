package net.dsdstudio.usedmarket;

import net.dsdstudio.usedmarket.models.GreetingMessage;
import net.dsdstudio.usedmarket.models.HelloMessage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * usedmarket-collector net.dsdstudio.usedmarket
 *
 * @author : bhkim
 * @since : 2015-01-22 오후 10:12
 */
@ComponentScan(basePackages = "net.dsdstudio.usedmarket")
@EnableAutoConfiguration
@Controller
public class MainController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public GreetingMessage greeting(HelloMessage s) throws Exception {
        return new GreetingMessage(LocalDateTime.now().toString() + " : " + s.getName());
    }
}
