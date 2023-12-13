package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@TableName("user")
public class UserPlus {

    @TableId(value = "id", type = IdType.AUTO)
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
    @TableField("battle")
    private boolean battle;
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
    @TableField("steam_id")
    private String steamId;
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
    private BigDecimal lucky;
    @TableField("accessory_lucky")
    private BigDecimal accessoryLucky;
    // 1正常 0删除
    @TableField("is_delete")
    private int isDelete = 1;
    @TableField(value = "cap_restrictions", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal capRestrictions;
    private Tag tag;
    @TableField("gear")
    private int gear = 1;
    private String ct;
    /**
     * 人脸核身增加真实姓名、身份证号码、累计充值100V币实名认证
     */
    //真实姓名
    @TableField("real_name")
    private String realName;
    //身份证号码
    @TableField("id_no")
    private String idNo;
    //累计充值100V币实名认证
    @TableField("first_face_state")
    private Integer firstFaceState;
    //是否实名认证(0:否,1:是)
    @TableField("real_name_state")
    private Integer realNameState;
}
