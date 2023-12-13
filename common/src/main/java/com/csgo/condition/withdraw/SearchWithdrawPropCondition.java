package com.csgo.condition.withdraw;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.withdraw.WithdrawPropDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchWithdrawPropCondition extends Condition<WithdrawPropDTO> {

    private String userName;

    //账号类型
    private Integer flag;

    private Date startDate;

    private Date endDate;
}
