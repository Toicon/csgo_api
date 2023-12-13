package com.csgo.web.request.roll;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Getter
@Setter
public class RollUserSelectRequest extends PageRequest {

    @NotNull(message = "房间ID不能为空")
    private Integer rollId;

    private String keyword;

}
