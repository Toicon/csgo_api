package com.csgo.service.blind;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.blind.SearchBlindBoxCondition;
import com.csgo.domain.GiftProduct;
import com.csgo.domain.plus.blind.BlindBoxDTO;
import com.csgo.domain.plus.blind.BlindBoxPlus;
import com.csgo.domain.plus.blind.BlindBoxProductPlus;
import com.csgo.domain.plus.gift.GiftProductRecordPlus;
import com.csgo.mapper.BlindBoxProductMapper;
import com.csgo.mapper.GiftProductMapper;
import com.csgo.mapper.plus.blind.BlindBoxPlusMapper;
import com.csgo.mapper.plus.blind.BlindBoxProductPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductRecordPlusMapper;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;
import com.csgo.web.request.blindbox.RelateGiftView;

@Service
public class BlindBoxService {

    @Autowired
    private BlindBoxPlusMapper blindBoxPlusMapper;
    @Autowired
    private BlindBoxProductMapper blindBoxProductMapper;
    @Autowired
    private BlindBoxProductPlusMapper blindBoxProductPlusMapper;
    @Autowired
    private GiftProductMapper giftProductMapper;
    @Autowired
    private GiftProductRecordPlusMapper giftProductRecordPlusMapper;

    public BlindBoxPlus get(int id) {
        return blindBoxPlusMapper.selectById(id);
    }

    public BlindBoxPlus findByName(String name) {
        return blindBoxPlusMapper.get(name);
    }

    @Transactional
    public void insert(BlindBoxPlus blindBoxPlus) {
        BlindBoxPlus boxPlus = blindBoxPlusMapper.get(blindBoxPlus.getName());
        if (null != boxPlus) {
            throw new BusinessException(ExceptionCode.BIND_BOX_EXISTS);
        }
        blindBoxPlus.setAddTime(new Date());
        blindBoxPlusMapper.insert(blindBoxPlus);
    }

    @Transactional
    public void update(BlindBoxPlus blindBoxPlus) {
        blindBoxPlus.setUpdateTime(new Date());
        blindBoxPlusMapper.updateById(blindBoxPlus);
    }

    public void deleteBath(List<Integer> ids) {
        for (Integer id : ids) {
            blindBoxPlusMapper.deleteById(id);
            blindBoxProductMapper.deleteByBoxId(id);
        }
    }

    public Page<BlindBoxDTO> pagination(SearchBlindBoxCondition condition) {
        return blindBoxPlusMapper.pagination(condition.getPage(), condition);
    }

    @Transactional
    public void batchInsert(List<RelateGiftView> relateGifts, int typeId) {
        relateGifts.forEach(relateGift -> {
            BlindBoxPlus blindBox = syncBlindBox(null, relateGift, typeId);
            syncBlindBoxGiftProduct(relateGift, blindBox);
        });
    }

    @Transactional
    public void syncByGift(BlindBoxPlus blindBox, RelateGiftView relateGift) {
        syncBlindBox(blindBox, relateGift, blindBox.getTypeId());
        syncBlindBoxGiftProduct(relateGift, blindBox);
    }

    private BlindBoxPlus syncBlindBox(BlindBoxPlus blindBox, RelateGiftView gift, int typeId) {
        boolean insert = false;
        if (blindBox == null) {
            blindBox = new BlindBoxPlus();
            blindBox.setAddTime(new Date());
            insert = true;
        }
        blindBox.setUpdateTime(new Date());
        blindBox.setPrice(gift.getPrice());
        blindBox.setImg(gift.getImg());
        blindBox.setName(gift.getName());
        blindBox.setTypeId(typeId);
        blindBox.setType(2);
        blindBox.setSortId(999);
        blindBox.setBoxImg(gift.getBoxImg());
        if (insert) {
            blindBoxPlusMapper.insert(blindBox);
            return blindBox;
        }
        blindBoxPlusMapper.updateById(blindBox);
        return blindBox;
    }

    private void syncBlindBoxGiftProduct(RelateGiftView gift, BlindBoxPlus blindBox) {
        List<GiftProduct> giftProductList = giftProductMapper.getListByGiftId(gift.getId());
        blindBoxProductPlusMapper.deleteByBoxId(blindBox.getId());

        List<GiftProductRecordPlus> giftProductRecords = giftProductRecordPlusMapper.findByGiftIds(Collections.singletonList(blindBox.getGiftId()), null);
        Map<Integer, GiftProductRecordPlus> giftProductRecordMap = giftProductRecords.stream().collect(Collectors.toMap(GiftProductRecordPlus::getGiftProductId, record -> record, (value1, value2) -> value1));

        giftProductList.forEach(p -> {
            BlindBoxProductPlus blindBoxProduct = new BlindBoxProductPlus();
            blindBoxProduct.setAddTime(new Date());
            blindBoxProduct.setUpdateTime(new Date());
            blindBoxProduct.setGiftProductId(p.getId());
            if (giftProductRecordMap.containsKey(p.getId())) {
                blindBoxProduct.setOutProbability(Integer.parseInt(giftProductRecordMap.get(p.getId()).getOutProbability()));
            }
            blindBoxProduct.setProbability(BigDecimal.valueOf(0.2));
            blindBoxProduct.setImgUrl(p.getImg());
            blindBoxProduct.setPrice(p.getPrice());
            blindBoxProduct.setBlindBoxId(blindBox.getId());
            blindBoxProductPlusMapper.insert(blindBoxProduct);
        });
    }

    public List<BlindBoxPlus> findWithGift() {
        return blindBoxPlusMapper.findWithGift();
    }
}
