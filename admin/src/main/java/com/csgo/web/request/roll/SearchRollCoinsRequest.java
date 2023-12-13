package com.csgo.web.request.roll;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Setter
@Getter
public class SearchRollCoinsRequest extends PageRequest {
    private Integer rollId;
}
