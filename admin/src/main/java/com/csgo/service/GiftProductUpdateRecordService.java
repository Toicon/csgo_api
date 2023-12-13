package com.csgo.service;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchProductRecordCondition;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.gift.GiftProductUpdateRecord;
import com.csgo.mapper.BlindBoxProductMapper;
import com.csgo.mapper.BlindBoxTurnMapper;
import com.csgo.mapper.LuckyProductMapper;
import com.csgo.mapper.RandomProductMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductUpdateRecordMapper;
import com.csgo.mapper.plus.roll.RollGiftPlusMapper;

/**
 * @author admin
 */
@Service
public class GiftProductUpdateRecordService {
    @Autowired
    private GiftProductUpdateRecordMapper giftProductUpdateRecordMapper;
    @Autowired
    private LuckyProductMapper luckyProductMapper;
    @Autowired
    private RandomProductMapper randomProductMapper;
    @Autowired
    private BlindBoxProductMapper blindBoxProductMapper;
    @Autowired
    private BlindBoxTurnMapper blindBoxTurnMapper;
    @Autowired
    private RollGiftPlusMapper rollGiftPlusMapper;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;


    public Page<GiftProductUpdateRecord> pagination(SearchProductRecordCondition condition) {
        return giftProductUpdateRecordMapper.pagination(condition.getPage(), condition);
    }

    @Transactional
    public void batchUpdate(List<Integer> ids) {
        for (Integer id : ids) {
            update(id);
        }
    }

    @Transactional
    public void update(int recordId) {
        GiftProductUpdateRecord record = giftProductUpdateRecordMapper.selectById(recordId);
        if (null == record) {
            return;
        }
        GiftProductPlus productPlus = giftProductPlusMapper.selectById(record.getProductId());
        if (null == productPlus) {
            return;
        }
        productPlus.setOriginAmount(record.getUpdateOriginPrice());
        productPlus.setPrice(record.getUpdatePrice());
        productPlus.setUpdatedAt(new Date());
        giftProductPlusMapper.updateById(productPlus);
        record.setCanUpdate(true);
        giftProductUpdateRecordMapper.updateById(record);
        blindBoxProductMapper.updatePriceByGiftProductId(productPlus.getId(), record.getUpdatePrice());
        // 更新盒子开启商品信息
        blindBoxTurnMapper.updateByGiftProductId(productPlus.getId(), record.getUpdatePrice());
        // 更新幸运饰品价格
        luckyProductMapper.updatePriceByGiftProductId(productPlus.getId(), record.getUpdatePrice());
        // 更新随机饰品价格
        randomProductMapper.updatePriceByGiftProductId(productPlus.getId(), record.getUpdatePrice());
        //更新roll福利礼包信息
        rollGiftPlusMapper.updateByGiftProductId(productPlus.getId(), record.getUpdatePrice(), productPlus.getImg(), productPlus.getName(), productPlus.getGrade());
    }
}
