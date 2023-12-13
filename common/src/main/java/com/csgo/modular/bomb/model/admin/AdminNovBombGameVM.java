package com.csgo.modular.bomb.model.admin;

import com.csgo.framework.model.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdminNovBombGameVM extends PageParam {

    private Integer userId;

    private String userName;

    private String userNameQ;

    private Date startDate;
    private Date endDate;

}
