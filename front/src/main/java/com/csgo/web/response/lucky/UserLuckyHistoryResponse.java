package com.csgo.web.response.lucky;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class UserLuckyHistoryResponse {
    private Integer id;
    private String luckNumber;
    private Integer userId;
    private Integer probability;
    private Integer luckyId;
    private BigDecimal price;
    private String randomProductName;
    private String randomProductImg;
    private Integer randomProductId;
    private BigDecimal randomProductPrice;
    private String luckyProductName;
    private Integer luckyProductId;
    private String luckyProductImg;
    private BigDecimal luckyProductPrice;
    private Integer isLucky;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;
    private String img;
    private String name;
    private Integer randomMessageId;
    private Integer luckyMessageId;
}
