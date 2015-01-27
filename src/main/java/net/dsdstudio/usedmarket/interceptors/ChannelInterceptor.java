package net.dsdstudio.usedmarket.interceptors;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

/**
 * usedmarket-collector net.dsdstudio.usedmarket.interceptors
 *
 * @author : bhkim
 * @since : 2015-01-27 20:18
 */
public class ChannelInterceptor extends ChannelInterceptorAdapter {
    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accesor = StompHeaderAccessor.wrap(message);
        if (SimpMessageType.SUBSCRIBE == accesor.getCommand().getMessageType()) {
            System.out.println("SUBSCRIBE => " + channel);
        }

        return super.postReceive(message, channel);
    }

    @Override
    public boolean preReceive(MessageChannel channel) {

        return super.preReceive(channel);
    }
}
