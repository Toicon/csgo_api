package com.csgo.config.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * 测试
 */
@Component
@Slf4j
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    /**
     * 在事件触发的时候调用这个方法
     */
    public void onApplicationEvent(SessionSubscribeEvent event) {
        //通过event获得Message
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("【SubscribeEventListener监听器事件 类型】" + headerAccessor.getCommand().getMessageType());
        //必须在HandshakeInterceptor拦截之后放入了sessionid才可以取到
        log.info("【SubscribeEventListener监听器事件 sessionId】" + headerAccessor.getSessionAttributes().get("sessionId"));


        /**
         * 在用户断开连接的时候触发 一般会使用到该监听器
         */
        //获得上面拦截器放进去的userId
        //String userId= headerAccessor.getSessionAttributes().get("userId");

        //在redis中把用户状态设置下线 30分钟没上线就删除
//        redisTemplate.set(userid,true,30, TimeUnit.MINUTES);

    }
}
