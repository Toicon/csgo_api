package com.csgo.web.response.box;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class TreasureBoxGiftResponse {

    private int id;
    private String name;
    private BigDecimal price;
    private String grade;
    private String img;
    private String treasureBoxName;
    private String treasureBoxImg;
    private String treasureBoxHalationImg;
    private String treasureBoxBgImg;
    private List<String> probabilities;
    private Boolean wishSwitch;
    private Boolean newPeopleSwitch;

    private Integer keyProductId;
    @ApiModelProperty(value = "钥匙饰品名称")
    private String keyProductName;
    private Integer keyNum;
    @ApiModelProperty(value = "用户钥匙数量")
    private Integer userKeyNum;

}
