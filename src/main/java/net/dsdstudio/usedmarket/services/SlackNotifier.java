package net.dsdstudio.usedmarket.services;

import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by bhkim on 2016. 1. 22..
 */
@Component
@Configuration
public class SlackNotifier {
    @Value("${slack.token}")
    private String token;
    private final String baseUrl = "https://slack.com/api/chat.postMessage";

    public final static String CHANNEL_NOTIFY = "#bhkim_notify";

    public SlackNotifier() {
    }

    @PostConstruct
    public void init() {
        System.out.println("slack token => " + this.token);
    }

    public void notifyMessage(String channel, String msg) {
        System.out.println("notifying message => " + channel);
        Async.newInstance().execute(
                Request.Get(baseUrl + "?token=" + token + "&channel=" + encodeUrl(channel) + "&pretty=1&text=" + encodeUrl(msg))
        );
    }

    private String encodeUrl(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
