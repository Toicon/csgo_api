package com.csgo.support.ZBT;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/9/18
 */
@Setter
@Getter
public class CheckCreateDTO {
    private Integer appId; //游戏id,示例值(730)
    private String tradeUrl; //交易链接
    private Integer type; //不同检测场景，1为购买，2为出售,示例值(1)

}
