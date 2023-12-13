package com.csgo.web.response.blindbox;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class RoomBoxResponse {
    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;
    @ApiModelProperty(value = "房间编号", required = true)
    private String roomNum;
    @ApiModelProperty(value = "盲盒价格", required = true)
    private BigDecimal price;
    @ApiModelProperty(value = "盲盒名称", required = true)
    private String name;
    @ApiModelProperty(value = "盲盒图片", required = true)
    private String img;
    @ApiModelProperty(value = "等级", required = true)
    private Integer grade;
    @ApiModelProperty(value = "盲盒ID", required = true)
    private Integer blindBoxId;
    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;
    private String boxImg;
}
