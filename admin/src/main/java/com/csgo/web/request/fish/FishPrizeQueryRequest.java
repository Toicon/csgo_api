package com.csgo.web.request.fish;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Data;

/**
 * 钓鱼玩法--用户奖励管理
 */
@Data
public class FishPrizeQueryRequest extends PageRequest {
    /**
     * 账号
     */
    private String userName;


}
