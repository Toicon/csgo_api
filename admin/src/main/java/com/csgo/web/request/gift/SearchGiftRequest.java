package com.csgo.web.request.gift;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/5/26
 */
@Setter
@Getter
public class SearchGiftRequest extends PageRequest {
    private String name;

    private Integer typeId;

    private boolean membershipGrade;
}
