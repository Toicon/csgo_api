package com.csgo.web.request.box;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchTreasureBoxRequest extends PageRequest {

    private String name;
    private String giftName;
}
