package com.csgo.domain.plus.gift;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 根据指定金额返回最接近商品记录（随机饰品）
 *
 * @author admin
 */
@Data
public class RandomGiftProductDTO {

    /**
     * 奖品商品id
     */
    private Integer giftProductId;
    /**
     * 奖品名称
     */
    private String giftProductName;

    /**
     * 奖品价格
     */
    private BigDecimal giftProductPrice;
    /**
     * 奖品图片
     */
    private String giftProductImg;
    /**
     * 奖品标签
     */
    private String exteriorName;

}
