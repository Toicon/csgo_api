package com.csgo.web.request.roll;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchRollUserRequest extends PageRequest {

    private Integer rollId;
    private Integer flag;
    private String userName;
}
