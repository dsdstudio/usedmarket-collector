package net.dsdstudio.usedmarket.services;

import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by bhkim on 2016. 1. 22..
 */
@Component
public class SlackNotifier {

    @Value("${slack.token}")
    private String token;
    private final String baseUrl = "https://slack.com/api/chat.postMessage";

    public final static String CHANNEL_NOTIFY = "#bhkim_notify";

    public SlackNotifier() {
    }

    public void notifyMessage(String channel, String msg) {
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
