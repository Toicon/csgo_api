package com.csgo.web.request.blindbox;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class BlindBoxRoomPaginationRequest extends PageRequest {

    private int type;
}
