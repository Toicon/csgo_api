package com.csgo.web.request.roll;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Setter
@Getter
public class SearchRollGiftPlusRequest extends PageRequest {
    private Integer rollId;
    private boolean calculateAmount = true;
}
