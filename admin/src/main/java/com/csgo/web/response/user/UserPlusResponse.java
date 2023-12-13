package com.csgo.web.response.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class UserPlusResponse {
    private Integer id;
    private String userName;
    private String userNumber;
    private String password;
    private String name;
    private String sex;
    private Integer flag;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createdAt;
    private String extensionCode;
    private String extensionUrl;
    private Integer luckyValue;
    private String phone;
    private String steam;
    private String emil;
    private String transactionlink;
    private BigDecimal balance;
    /**
     * 余额(银币)
     */
    private BigDecimal diamondBalance;
    private int isonline;
    private int roleId;
    private String roleName;
    private BigDecimal payMoney;
    private BigDecimal extract;
    private int extraNum;
    private Boolean isroll;
    private Boolean isRollNoLimit;
    private String img;
    private Integer parentId;
    private String parentName;
    private String parentUserName;
    private Integer type;
    private int isDelete;
    private Boolean frozen;
    private Integer status;
    private BigDecimal lucky;
    private BigDecimal accessoryLucky;
    private Integer recommendCount;
    private BigDecimal profit;
    private BigDecimal capRestrictions;
    private int gear;
    private String ct;
    private boolean battle;
    //用户VIP等级
    private Integer level;
    private boolean innerRecharge;
    private boolean whiteInnerRecharge;
    //后台用户id
    private Integer adminUserId;
    //是否实名认证(0:否,1:是)
    private Integer realNameState;
    //是否测试开箱白名单
    private boolean whiteAnchorRecharge;

}
