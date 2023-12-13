package com.csgo.web.request.gift;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/7/1
 */
@Setter
@Getter
public class SearchGiftProductRequest extends PageRequest {
    private String giftProductName;

    private String type;

    private String orderStock;
}
