package com.csgo.web.request.fish;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Data;

/**
 * 钓鱼玩法--鱼饵配置信息
 */
@Data
public class FishBaitConfigQueryRequest extends PageRequest {
    /**
     * 鱼饵名称
     */
    private String baitName;

    /**
     * 场次类型(1:初级，2:中级，3:高级)
     */
    private Integer sessionType;

}
