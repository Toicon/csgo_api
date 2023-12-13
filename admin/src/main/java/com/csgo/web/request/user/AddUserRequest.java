package com.csgo.web.request.user;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class AddUserRequest {
    /**
     * 关联账号(后台管理账号)
     */
    private Integer adminUserId;
    private String userName;
    private String password;
    private String name;
    private Integer flag;
    private String sex;
    private String steam;
    private BigDecimal balance;
    private BigDecimal diamondBalance;
    private Integer extraNum;
    private String img;
    private Boolean isroll;
    private Boolean isRollNoLimit;
    private String extensionCode;
    private String extensionUrl;
    private BigDecimal capRestrictions;
    private String tag;
    private Integer gear;
    private boolean battle;
    private String parentUserName;
}
