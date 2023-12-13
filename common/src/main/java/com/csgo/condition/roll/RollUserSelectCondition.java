package com.csgo.condition.roll;

import com.csgo.condition.Condition;
import com.csgo.domain.RollUser;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Getter
@Setter
public class RollUserSelectCondition extends Condition<RollUser> {

    @NotNull(message = "房间ID不能为空")
    private Integer rollId;

    private String keyword;

}
