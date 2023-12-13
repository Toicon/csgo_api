package com.csgo.modular.product.logic;

import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.mapper.LuckyProductMapper;
import com.csgo.mapper.RandomProductMapper;
import com.csgo.mapper.plus.roll.RollGiftPlusMapper;
import com.csgo.modular.common.sms.logic.SmsLogic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GiftProductSyncLogic {

    private final LuckyProductMapper luckyProductMapper;
    private final RandomProductMapper randomProductMapper;
    private final RollGiftPlusMapper rollGiftPlusMapper;

    private final SmsLogic smsLogic;

    public void syncRelation(GiftProductPlus gp) {
        // 更新盒子商品价格
        //blindBoxProductMapper.updatePriceByGiftProductId(gp.getId(), price);
        // 更新盒子开启商品信息
        //blindBoxTurnMapper.updateByGiftProductId(gp.getId(), price);

        // 更新幸运饰品价格
        luckyProductMapper.updatePriceByGiftProductId(gp.getId(), gp.getPrice());
        // 更新随机饰品价格
        randomProductMapper.updatePriceByGiftProductId(gp.getId(), gp.getPrice());
        //更新roll福利礼包信息
        rollGiftPlusMapper.updateByGiftProductId(gp.getId(), gp.getPrice(), gp.getImg(), gp.getName(), gp.getGrade());
    }

}
