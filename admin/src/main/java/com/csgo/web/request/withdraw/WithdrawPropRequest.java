package com.csgo.web.request.withdraw;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Created by Admin on 2021/5/22
 */
@Setter
@Getter
public class WithdrawPropRequest {

    private Integer id;
    @NotBlank(message = "审批状态")
    private String status;
}
