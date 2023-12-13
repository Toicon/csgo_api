package com.csgo.condition.prize;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.user.UserPrizeDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserPrizeDTOCondition extends Condition<UserPrizeDTO> {
    private Integer userId;
    private String userName;
    private String giftType;
    private String giftName;
    private String giftProductName;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private Date startDate;
    private Date endDate;
}
