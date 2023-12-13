package com.csgo.config.socket;

import com.csgo.domain.socket.InMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocketMessage {

    private Integer userId;
    private String dest;
    private InMessage message;

    public SocketMessage(String dest, InMessage message) {
        this.dest = dest;
        this.message = message;
    }
}
