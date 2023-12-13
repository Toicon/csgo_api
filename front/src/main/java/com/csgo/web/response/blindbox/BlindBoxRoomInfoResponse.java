package com.csgo.web.response.blindbox;

import com.csgo.web.response.user.UserResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class BlindBoxRoomInfoResponse {

    private List<RoomBoxResponse> roomBoxList;
    private BlindBoxRoomResponse boxRoom;
    private List<UserResponse> userList;
    private List<List<BlindBoxTurnResponse>> blindBoxTurn;
}
