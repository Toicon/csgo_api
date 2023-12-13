package com.csgo.service;

import com.csgo.domain.socket.InMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SimpMessagingTemplateFacde {

    @Autowired
    private SimpMessagingTemplate template;

    /*
     * 简单的指定消息到目的地：广播、单播
     *
     * @param dest:目的地的路径
     * @param message：给OutMessage提供content和from
     */
    public void convertAndSend(String dest, InMessage message) {
        log.info("开始推送【" + message.getStatus() + "】-----------------");
        template.convertAndSend(dest, message);
    }

    public void convertAndSendToUser(String userId, String dest, InMessage message) {
        log.info("开始推送【" + message.getStatus() + "】-----------------");
        template.convertAndSendToUser(userId, dest, message);
    }

    public void convertAndSendToUser(int userId, String dest, InMessage message) {
        log.info("开始推送【" + message.getStatus() + "】-----------------");
        template.convertAndSendToUser(String.valueOf(userId), dest, message);
    }

    /*
     * 心跳检测 直接给源路径返回"pang"作标志
     */
    public void sendPong(InMessage message) {
        template.convertAndSend(message.getTo());
    }
}
