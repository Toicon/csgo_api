package com.csgo.web.response.blindbox;

import com.echo.framework.platform.web.response.PageResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class BlindBoxHistoryResponse {

    private PageResponse<BlindBoxRoomResponse> pagination;
    private BlindBoxRoomResponse blindBoxRoom;
    private BlindBoxRoomResponse toDayBlindBoxRoom;
}
