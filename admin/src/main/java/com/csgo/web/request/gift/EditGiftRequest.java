package com.csgo.web.request.gift;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/5/24
 */
@Setter
@Getter
public class EditGiftRequest {
    private int id;
    private String type;
    private Integer boxId;
    private Integer typeId;
    private BigDecimal price;
    private String name;
    private String img;
    private String grade;
    private String giftPassword;
    private Boolean hidden;
    private String showProbability;
    private BigDecimal threshold;
    private Integer membershipGrade;
    private Integer probabilityType;
    private Boolean wishSwitch;
    private Boolean newPeopleSwitch;
    private Integer keyProductId;
    private Integer keyNum;
}
