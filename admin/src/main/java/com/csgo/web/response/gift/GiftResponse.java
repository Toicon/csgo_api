package com.csgo.web.response.gift;

import com.csgo.domain.GiftGrade;
import com.csgo.domain.plus.gift.GiftType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class GiftResponse {

    private Integer id;
    private String name;

    private String type;

    private Integer typeId;

    private BigDecimal price;

    private String img;

    private Date createdAt;

    private Date updatedAt;

    private String grade;

    private Boolean hidden;

    private String showProbability;

    private Integer boxId;

    private String boxImg;

    private BigDecimal threshold;

    private GiftType giftType;

    private GiftGrade giftGrade;

    private Integer membershipGrade;

    private Integer giftNum;

    private Boolean wishSwitch;

    private Boolean newPeopleSwitch;

    private Integer probabilityType;

    private Integer keyProductId;
    private Integer keyNum;

}
