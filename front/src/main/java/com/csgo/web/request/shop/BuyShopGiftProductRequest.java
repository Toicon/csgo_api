package com.csgo.web.request.shop;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class BuyShopGiftProductRequest {
    private int giftProductId;
    private String token;
}
