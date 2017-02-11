package net.dsdstudio.usedmarket.services;

import net.dsdstudio.usedmarket.BoardData;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
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

    private String encodeUrl(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyMessage(Message msg) {
        System.out.println("notifying message => " + msg.getChannel());
        Async.newInstance().execute(
                Request.Get(baseUrl + "?token=" + token + "&channel=" + encodeUrl(msg.getChannel()) + "&pretty=1&text=" + encodeUrl(msg.getMsg()))
        );
    }

    public static class Message {
        private String channel;
        private String msg;

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Message(BoardData b) {
            channel = SlackNotifier.CHANNEL_NOTIFY;
            msg = b.dataType + " [" + b.date + "] " + b.subject + "\n" + b.getDetailUrl();
        }
    }

}
