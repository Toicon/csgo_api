package com.csgo.domain.user;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户信息
 */
@Data
@NoArgsConstructor
public class User {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "用户名", required = true)
    private String userName;

    @ApiModelProperty(value = "用户编码", required = true)
    private String userNumber;

    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @ApiModelProperty(value = "昵称", required = true)
    private String name;

    @ApiModelProperty(value = "性别", required = true)
    private String sex;

    @ApiModelProperty(value = "标识符,true-内部用户,false-散户", required = true)
    private Integer flag;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date createdAt;

    @ApiModelProperty(value = "修改时间", required = true)
    private Date updatedAt;

    @ApiModelProperty(value = "推广码", required = true)
    private String extensionCode;

    @ApiModelProperty(value = "推广链接", required = true)
    private String extensionUrl;

    @ApiModelProperty(value = "幸运值", required = true)
    private Integer luckyValue;

    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @ApiModelProperty(value = "steam账号", required = true)
    private String steam;

    @ApiModelProperty(value = "邮箱", required = true)
    private String emil;

    @ApiModelProperty(value = "交易链接", required = true)
    private String transactionlink;

    @ApiModelProperty(value = "余额", required = true)
    private BigDecimal balance;

    @ApiModelProperty(value = "银币余额", required = true)
    private BigDecimal diamondBalance;

    @ApiModelProperty(value = "用户是否在线 0 不在线  1 在线", required = true)
    private int isonline;

    @ApiModelProperty(value = "角色id", required = true)
    private int roleId;

    @ApiModelProperty(value = "角色名称", required = true)
    private String roleName;

    @ApiModelProperty(value = "总充值金额", required = true)
    private BigDecimal pay_money;

    @ApiModelProperty(value = "总提取金额", required = true)
    private BigDecimal extract;

    @ApiModelProperty(value = "总提取金额", required = true)
    private int extraNum;

    @ApiModelProperty(value = "用户是否可以开roll房间0-不可以，1-可以", required = true)
    private Boolean isroll;
    private Boolean isRollNoLimit;

    @ApiModelProperty(value = "用户头像", required = true)
    private String img;
    @ApiModelProperty(value = "父类id", required = true)
    private Integer parentId;
    private Date invitedDate;
    @ApiModelProperty(value = "推广类型  0 无人推广  1推广码 2推广链接", required = true)
    private Integer type;
    @ApiModelProperty(value = "是否冻结账号  0 否  1是", required = true)
    private Boolean frozen;
    private Integer status;
    private BigDecimal lucky;
    private BigDecimal accessoryLucky;
    private Integer pageNum;
    private Integer pageSize;
    private Integer total;
    //登录验证token
    private String accessToken;
    private Integer recommendCount;
    // 积分
    private Integer integralCount;
    private Long startTime;
    private Long endTime;
    private String sort;
    // 1正常 0删除
    private int isDelete = 1;
    /**
     * 人脸核身增加真实姓名、身份证号码
     */
    //真实姓名
    private String realName;
    //身份证号码
    private String idNo;
    //累计充值100V币实名认证
    private Integer firstFaceState;

}
