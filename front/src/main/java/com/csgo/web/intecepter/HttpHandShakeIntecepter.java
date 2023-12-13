package com.csgo.web.intecepter;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 功能描述：http握手拦截器，最早执行
 * 可以通过这个类的方法获取resuest,和response 给后面使用
 */

public class HttpHandShakeIntecepter implements HandshakeInterceptor {


    /**
     * 在握手之前执行该方法, 继续握手返回true, 中断握手返回false. 通过attributes参数设置WebSocketSession的属性
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        String queryParamList = request.getURI().getQuery();
        String userId = null;
        String[] split = queryParamList.split("&");
        if (split.length > 0) {
            for (String queryParam : split) {
                String[] param = queryParam.split("=");
                if (param != null && param.length > 0 && param[0].equals("userId")) {
                    userId = param[1];
                    break;
                }
            }
        }

        //如果userid为空 或者redis中不存在userid 返回未授权错误码（redis自行整合 在登陆时存入redis）
//        if (StringUtils.isBlank(userid) || !redisTemplate.haskey(userid)) {
//            response.setStatusCode( HttpStatus.UNAUTHORIZED );
//            return false;
//        }

        //把该usrid存入stompAccessHeader 供DisConnect监听器断开连接时从redis中找到对应的并把online设置为false 如果5分钟没没有更新为true就在redis中删掉该用户
        //在断开连接的监听器通过stompHeaderAccessor.getSessionAttributes().get("userid")取得
        attributes.put("userId", userId);
        //把username存入attributes 方便在下面的config中取出后设置成Principal 当有异常发送时用@SendToUser 不然不知道目的地

        //如果是掉线重新设为true
//        if(!redisTemplate.get(userid)){
//            redisTemplate.set(userid,true);
//        }

        return true;
    }


    /**
     * 在握手之后执行该方法. 无论是否握手成功都指明了响应状态码和相应头.
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param exception
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        //该方法一般没有 所以不实现
    }

}
