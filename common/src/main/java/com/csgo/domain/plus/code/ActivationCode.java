package com.csgo.domain.plus.code;

import com.baomidou.mybatisplus.annotation.TableField;
import com.csgo.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class ActivationCode extends BaseEntity {
    @TableField("user_id")
    private Integer userId;

    @TableField("gift_product_id")
    private Integer giftProductId;

    @TableField("user_name")
    private String userName;

    @TableField("flag")
    private Integer flag;

    @TableField("product_name")
    private String productName;

    @TableField(value = "product_type", typeHandler = ProductType.TypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private ProductType productType;

    @TableField("price")
    private BigDecimal price;

    @TableField("cd_key")
    private String cdKey;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("receive_date")
    private Date receiveDate;

    /**
     * 目标账号
     */
    @TableField("target_user_name")
    private String targetUserName;
    /**
     * 目标账号id
     */
    @TableField("target_user_id")
    private Integer targetUserId;

}
