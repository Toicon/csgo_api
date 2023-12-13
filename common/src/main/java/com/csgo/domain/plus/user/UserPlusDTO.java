package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class UserPlusDTO {

    private Integer id;
    @TableField("user_name")
    private String userName;
    @TableField("password")
    private String password;
    @TableField("name")
    private String name;
    @TableField("sex")
    private String sex;
    @TableField("flag")
    private Integer flag;
    @TableField("created_at")
    private Date createdAt;
    @TableField("updated_at")
    private Date updatedAt;
    @TableField("extension_code")
    private String extensionCode;
    @TableField("extension_url")
    private String extensionUrl;
    @TableField("lucky_value")
    private Integer luckyValue;
    @TableField("phone")
    private String phone;
    @TableField("steam")
    private String steam;
    @TableField("emil")
    private String emil;
    @TableField("transactionlink")
    private String transactionlink;
    @TableField("balance")
    private BigDecimal balance;
    @TableField("diamond_balance")
    private BigDecimal diamondBalance;
    @TableField("isonline")
    private int isonline;
    @TableField("pay_money")
    private BigDecimal payMoney;
    @TableField("extract")
    private BigDecimal extract;
    @TableField("extra_num")
    private int extraNum;
    @TableField("isroll")
    private Boolean isroll;
    @TableField("is_roll_no_limit")
    private Boolean isRollNoLimit;
    @TableField("img")
    private String img;
    @TableField("parent_id")
    private Integer parentId;
    //被邀请日期
    @TableField("invited_date")
    private Date invitedDate;
    @TableField("user_number")
    private String userNumber;
    @TableField("type")
    private Integer type;
    @TableField("frozen")
    private boolean frozen;
    @TableField("status")
    private Integer status;
    @TableField("battle")
    private boolean battle;
    private BigDecimal lucky;
    @TableField("accessory_lucky")
    private BigDecimal accessoryLucky;
    // 1正常 0删除
    @TableField("is_delete")
    private int isDelete = 1;
    private Integer recommendCount;
    @TableField("cap_restrictions")
    private BigDecimal capRestrictions;
    @TableField("gear")
    private int gear;
    private String ct;
    private Integer level;
    //是否实名认证(0:否,1:是)
    private Integer realNameState;
}
