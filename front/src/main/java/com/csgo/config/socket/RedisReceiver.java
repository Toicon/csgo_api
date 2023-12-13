package com.csgo.config.socket;

import com.csgo.service.SimpMessagingTemplateFacde;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Service
@Slf4j
public class RedisReceiver {
    @Autowired
    private SimpMessagingTemplateFacde ws;

    public void receiveMessage(String message) {
        log.info("receive message: {}", message);
        SocketMessage socketMessage = JSON.fromJSON(message, SocketMessage.class);
        if (socketMessage.getUserId() != null) {
            ws.convertAndSendToUser(socketMessage.getUserId(), socketMessage.getDest(), socketMessage.getMessage());
            return;
        }
        ws.convertAndSend(socketMessage.getDest(), socketMessage.getMessage());
    }
}