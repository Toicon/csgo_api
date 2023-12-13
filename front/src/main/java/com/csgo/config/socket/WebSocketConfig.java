package com.csgo.config.socket;

import com.csgo.web.intecepter.HttpHandShakeIntecepter;
import com.csgo.web.intecepter.SocketChannelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
//开启对websocket的支持,使用stomp协议传输代理消息，
// 这时控制器使用@MessageMapping和@RequestMaping一样
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Autowired
    private SocketChannelInterceptor socketChannelInterceptor;

    /**
     * Endpoint：注册端点，发布或者订阅消息的时候需要连接此端点
     * Interceptors：前面的握手拦截器
     * AllowedOrigins：非必须，*表示允许其他域进行连接
     * withSockJS：表示开始sockejs支持
     */
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/endpoint-websocket").addInterceptors(new HttpHandShakeIntecepter())
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        //设置认证用户 从拦截器添加的userName取出
                        return null;
                    }
                })
                /*
                 * 因为Stomp是对websocket的实现 是异步发送 如果有异常 发送者将无法感知  设置完这个后在发送消息异常时  就会调用下面的接收器 把然后把该异常可以返回给消息的发送者 让前端知道发送异常并告知
                 *  @MessageExceptionHandler(Exception.class)
                 *  @SendToUser("/error/quene") //会发送到DefaultHandshakeHandler设置的Principal： /Principal/error/quene
                 *  public Exception handleExceptions(Exception t){
                 *		t.printStackTrace();
                 *		return t;
                 *  }
                 */

                .setAllowedOrigins("*").withSockJS();
    }

    /**
     * 配置消息代理(中介)
     * enableSimpleBroker 服务端推送给客户端的路径前缀
     * setApplicationDestinationPrefixes 客户端发送数据给服务器端的一个前缀
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/chat", "/user");
        registry.setApplicationDestinationPrefixes("/app");

    }

    /**
     * 消息传输参数配置
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(8192) //设置消息字节数大小
                .setSendBufferSizeLimit(8192)//设置消息缓存大小
                .setSendTimeLimit(10000); //设置消息发送时间限制毫秒
    }


    /**
     * 配置客户端入站通道拦截器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4) //设置消息输入通道的线程池线程数
                .maxPoolSize(8)//最大线程数
                .keepAliveSeconds(60);//线程最大空闲时间
        //前面配置的spring channel拦截器 过时不建议再使用
        registration.interceptors(socketChannelInterceptor);
    }

    /**
     * 配置客户端出站通道拦截器
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4).maxPoolSize(8);
        //前面配置的spring channel拦截器 过时不建议再使用
        registration.interceptors(socketChannelInterceptor);
    }
}
