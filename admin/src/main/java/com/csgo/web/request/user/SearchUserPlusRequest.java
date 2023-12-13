package com.csgo.web.request.user;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Setter
@Getter
public class SearchUserPlusRequest extends PageRequest {
    private String userName;
    private String inviteUser;
    private Boolean frozen;
    private Integer status;
    private Integer flag;
    private String startTime;
    private String endTime;
    private String tag;
    private boolean isInner;
    private String sort;
    private String sortBy;
    private Integer parentId;
    private String extensionCode;
}
