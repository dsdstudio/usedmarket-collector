package net.dsdstudio.umk;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * usedmarket-collector net.dsdstudio.umk
 *
 * @author : bhkim
 * @since : 2015-01-22 오후 10:12
 */
@ComponentScan(basePackages = "net.dsdstudio.umk")
@EnableAutoConfiguration

@Controller
public class MainController {

    @RequestMapping("/aa")
    public BoardData subscribe() throws Exception {
        return null;
    }
}
