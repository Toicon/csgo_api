package com.csgo.web.request.blindbox;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class AddBlindBoxRoomRequest {

    private BigDecimal price;
    private Integer personPattern;
    private Integer roomCount;
    private BigDecimal totalPrice;
    private List<Integer> boxIdList;
    private boolean needPwd;
    private String password;
}
