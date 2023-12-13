package com.csgo.web.intecepter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocketChannelInterceptor implements ChannelInterceptor {

    /**
     * 在完成发送之后进行调用，不管是否有异常发生，一般用于资源清理
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
    }


    /**
     * 在消息被实际发送到频道之前调用
     * 可以用作登陆验证
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        //1. 判断是否首次连接请求
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // return null;
        }
        //不是首次连接，已经成功登陆
        return message;
    }


    /**
     * 发送消息调用后立即调用 一般用于监听上下线
     */
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);//消息头访问器

        if (headerAccessor.getCommand() == null) return;// 避免非stomp消息类型，例如心跳检测

        switch (headerAccessor.getCommand()) {
            case CONNECT:
                //连接成功后操作...
                break;
            case DISCONNECT:
                //断开连接的操作...
                break;

            case SUBSCRIBE:
                break;
            case UNSUBSCRIBE:
                break;
            default:
                break;
        }

    }

}
