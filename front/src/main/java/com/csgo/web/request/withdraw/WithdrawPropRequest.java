package com.csgo.web.request.withdraw;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 2021/5/22
 */
@Setter
@Getter
public class WithdrawPropRequest {

    private List<Integer> messageIds;
}
