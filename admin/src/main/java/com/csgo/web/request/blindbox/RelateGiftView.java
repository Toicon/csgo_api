package com.csgo.web.request.blindbox;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class RelateGiftView {

    private int id;
    private String img;
    private String name;
    private BigDecimal price;
    private BigDecimal threshold;
    private String boxImg;
}
