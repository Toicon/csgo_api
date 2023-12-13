package com.csgo.web.response.lucky;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class LuckyProductRankingResponse {

    @ApiModelProperty(notes = "用户昵称", example = "11111")
    private String userName;
    @ApiModelProperty(notes = "用户头像路径", example = "11111")
    private String userImg;
    @ApiModelProperty(notes = "礼物名称", example = "11111")
    private String giftName;
    @ApiModelProperty(notes = "礼物图片路径", example = "11111")
    private String giftImg;
    @ApiModelProperty(notes = "幸运概率", example = "11111")
    private BigDecimal lucky;
    @ApiModelProperty(notes = "价格", example = "11111")
    private BigDecimal price;
    @ApiModelProperty(notes = "时间", example = "11111")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
