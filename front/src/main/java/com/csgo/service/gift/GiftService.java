package com.csgo.service.gift;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.csgo.domain.Gift;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.mapper.GiftMapper;
import com.csgo.mapper.plus.gift.GiftPlusMapper;
import com.csgo.service.lottery.support.LotteryDrawConstants;

@Service
public class GiftService {

    @Autowired
    private GiftMapper giftMapper;
    @Autowired
    private GiftPlusMapper giftPlusMapper;

    public int add(Gift gift) {

        return giftMapper.insertSelective(gift);
    }


    public int update(Gift gift, int id) {
        gift.setId(id);
        return giftMapper.updateByPrimaryKeySelective(gift);
    }

    public Gift queryGiftId(int id) {
        return giftMapper.selectByPrimaryKey(id);
    }

    public List<Gift> getList(Gift gift) {

        return giftMapper.getList(gift);
    }

    public List<Gift> getListByPage(Gift gift, int pageNum, int pageSize) {
        gift.setPageNum(pageNum);
        gift.setPageSize(pageSize);
        return giftMapper.getListByPage(gift);
    }

    public List<Gift> getTypeList(int typeId) {
        return giftMapper.getTypeList(typeId).stream().filter(gift -> !gift.getHidden()).collect(Collectors.toList());
    }


    public int delete(int id) {
        return giftMapper.deleteByPrimaryKey(id);
    }

    public void emptyCount() {
        giftMapper.emptyCount();
    }

    public void emptyCount2() {
        giftMapper.emptyCount2();
    }

    public List<GiftPlus> findLeGrade(Integer membershipGrade) {
        return giftPlusMapper.findLeGrade(membershipGrade);
    }

    public List<GiftPlus> findAllMembership() {
        return giftPlusMapper.findAllMembership();
    }

    public List<Gift> getAmmunitionGift() {
        return giftMapper.getTypeList(LotteryDrawConstants.AMMUNITION_GIFT_ID);
    }
}
