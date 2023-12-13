package com.csgo.service.roll;

import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.roll.RollCoins;
import com.csgo.domain.plus.roll.RollGiftPlus;
import com.csgo.domain.plus.roll.RollGiftType;
import com.csgo.domain.plus.roll.RollPlus;
import com.csgo.domain.plus.roll.RollUserPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.mapper.UserMessageGiftMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.roll.RollCoinsMapper;
import com.csgo.mapper.plus.roll.RollGiftPlusMapper;
import com.csgo.mapper.plus.roll.RollPlusMapper;
import com.csgo.mapper.plus.roll.RollUserPlusMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.backpack.logic.UserMessageLogic;
import com.csgo.service.UserBalanceService;
import com.csgo.service.UserMessageItemRecordService;
import com.csgo.service.UserMessageRecordService;
import com.csgo.support.GlobalConstants;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Service
public class RollDrawService {

    private static final Set<RollGiftType> INNER_GIFT_TYPES = Sets.newHashSet(RollGiftType.INNER, RollGiftType.CANCEL);
    private final Random random = new Random();

    @Autowired
    private RollPlusMapper rollPlusMapper;
    @Autowired
    private RollGiftPlusMapper rollGiftPlusMapper;
    @Autowired
    private RollCoinsMapper rollCoinsMapper;
    @Autowired
    private RollUserPlusMapper rollUserPlusMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private UserMessageLogic userMessageLogic;
    @Autowired
    private UserMessagePlusMapper userMessageMapper;
    @Autowired
    private UserMessageGiftMapper userMessageGiftMapper;
    @Autowired
    private GiftProductPlusMapper giftProductMapper;
    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private UserMessageItemRecordService userMessageItemRecordService;
    @Autowired
    private UserBalanceService userBalanceService;

    @Transactional
    public void draw(RollPlus roll) {
        //查询到当前对应的商品信息
        Integer rollId = roll.getId();
        List<RollGiftPlus> rollGiftList = rollGiftPlusMapper.find(rollId);
        List<RollCoins> rollCoins = rollCoinsMapper.findByRollIds(Collections.singletonList(rollId));

        if ((CollectionUtils.isEmpty(rollGiftList) && CollectionUtils.isEmpty(rollCoins))) {
            rollPlusMapper.updateById(roll);
            return;
        }
        roll.setNum(0);
        List<RollGiftPlus> unAppointGiftList = new ArrayList<>();
        List<RollUserPlus> rollUserList = rollUserPlusMapper.findByRollIds(Collections.singletonList(rollId));
        Set<Integer> innerAndUnAppointSet = rollUserPlusMapper.findInnerAndUnAppoint(roll.getId());
        //注：与rollUserList同源
        List<RollUserPlus> innerUsers = rollUserList.stream().filter(u -> innerAndUnAppointSet.contains(u.getUserId())).collect(Collectors.toList());
        boolean isProductGtUser = rollGiftList.size() > rollUserList.size();
        for (RollGiftPlus gift : rollGiftList) {
            if (INNER_GIFT_TYPES.contains(gift.getType())) {
                innerOrCancelHit(gift, roll, isProductGtUser, innerUsers);
                continue;
            }
            RollUserPlus rollUser = rollUserList.stream().filter(rollUserPlus -> gift.getId().equals(rollUserPlus.getRollgiftId()) && "1".equals(rollUserPlus.getIsappoint())).findFirst().orElse(null);
            if (null == rollUser) {
                unAppointGiftList.add(gift);
                continue;
            }
            hit(gift, rollUser, roll);
        }
        //未中奖集合
        List<RollUserPlus> unHitRollUserList = rollUserList.stream().filter(rollUserPlus -> GlobalConstants.ROLL_USER_UN_HIT.equals(rollUserPlus.getFlag())).collect(Collectors.toList());
        extract(unAppointGiftList, rollCoins, unHitRollUserList, roll);
        rollPlusMapper.updateById(roll);
    }

    private void innerOrCancelHit(RollGiftPlus gift, RollPlus roll, boolean isProductGtUser, List<RollUserPlus> innerUsers) {
        // 不开同时道具数量大于参与人数=这个奖品不送出去。
        if (RollGiftType.CANCEL.equals(gift.getType()) && isProductGtUser) {
            return;
        }
        innerHit(gift, roll, innerUsers);
    }

    private void innerHit(RollGiftPlus gift, RollPlus roll, List<RollUserPlus> innerUsers) {
        innerUsers = innerUsers.stream().filter(rollUserPlus -> GlobalConstants.ROLL_USER_UN_HIT.equals(rollUserPlus.getFlag())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(innerUsers)) {
            return;
        }
        randomHit(gift, innerUsers, roll);
    }

    private void extract(List<RollGiftPlus> rollGiftList, List<RollCoins> rollCoins, List<RollUserPlus> rollUserList, RollPlus roll) {
        for (RollGiftPlus gift : rollGiftList) {
            if (CollectionUtils.isEmpty(rollUserList)) {
                return;
            }
            RollUserPlus rollUser = randomHit(gift, rollUserList, roll);
            rollUserList.remove(rollUser);
        }
        for (RollCoins coins : rollCoins) {
            if (CollectionUtils.isEmpty(rollUserList)) {
                return;
            }
            int randoms = random.nextInt(rollUserList.size());
            //随机获取到对应的参奖者信息
            RollUserPlus rollUser = rollUserList.get(randoms);
            UserPlus user = userPlusMapper.selectById(rollUser.getUserId());
            coins.setUserId(rollUser.getUserId());
            rollCoinsMapper.updateById(coins);
            rollUser.setFlag(GlobalConstants.ROLL_USER_HIT);
            rollUser.setImg(user.getImg());
            rollUser.setRollgiftPrice(coins.getAmount());
            rollUser.setRollgiftName("金币");
            rollUser.setRollgiftImg(coins.getImg());
            rollUser.setUt(new Date());
            rollUserPlusMapper.updateById(rollUser);
            userBalanceService.add(user, coins.getAmount(), "ROLL福利金币");
            rollUserList.remove(randoms);
            roll.setNum(roll.getNum() + 1);
        }
    }

    private RollUserPlus randomHit(RollGiftPlus gift, List<RollUserPlus> rollUserList, RollPlus roll) {
        int randomNum = random.nextInt(rollUserList.size());
        //随机获取到对应的参奖者信息
        RollUserPlus rollUser = rollUserList.get(randomNum);
        hit(gift, rollUser, roll);
        return rollUser;
    }

    private void hit(RollGiftPlus gift, RollUserPlus rollUser, RollPlus roll) {
        UserPlus user = userPlusMapper.selectById(rollUser.getUserId());
        GiftProductPlus giftProduct = giftProductMapper.selectById(gift.getGiftProductId());
        rollUser.setRollId(gift.getRollId());
        rollUser.setFlag(GlobalConstants.ROLL_USER_HIT);
        rollUser.setImg(user.getImg());
        rollUser.setUsername(user.getName());
        rollUser.setRollname(roll.getRollName());
        rollUser.setRollgiftId(gift.getId());
        rollUser.setRollgiftImg(giftProduct.getImg());
        rollUser.setRollgiftName(giftProduct.getName());
        rollUser.setRollgiftPrice(giftProduct.getPrice());
        rollUser.setRollgiftGrade(giftProduct.getGrade());
        rollUser.setUt(new Date());
        rollUserPlusMapper.updateById(rollUser);
        addGiftProduct(giftProduct, rollUser.getUserId());
        roll.setNum(roll.getNum() + 1);
    }

    private void addGiftProduct(GiftProductPlus giftProduct, Integer userId) {
        //背包流水记录
        int recordId = userMessageRecordService.add(userId, "ROLL福利", "IN");

        UserMessagePlus userMessage = new UserMessagePlus();
        userMessage.setDrawDare(new Date());
        userMessage.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
        userMessage.setMoney(giftProduct.getPrice());
        userMessage.setImg(giftProduct.getImg());
        userMessage.setUserId(userId);
        userMessage.setProductName(giftProduct.getName());
        userMessage.setGameName("CSGO");
        userMessage.setGiftProductId(giftProduct.getId());
        userMessage.setGiftStatus("0");
        userMessageLogic.commonProcess(userMessage, giftProduct);

        userMessageMapper.insert(userMessage);
        //背包流水详情记录
        userMessageItemRecordService.add(recordId, userMessage.getId(), userMessage.getImg());

        UserPlus user = userPlusMapper.selectById(userId);
        UserMessageGift userMessageGift = new UserMessageGift();
        userMessageGift.setUserMessageId(userMessage.getId());
        userMessageGift.setUserId(userId);
        userMessageGift.setState(0);
        userMessageGift.setMoney(giftProduct.getPrice());
        userMessageGift.setGameName("CSGO");
        userMessageGift.setCt(new Date());
        userMessageGift.setImg(giftProduct.getImg());
        userMessageGift.setGiftProductName(giftProduct.getName());
        userMessageGift.setGiftProductId(giftProduct.getId());
        userMessageGift.setPhone(user.getPhone());
        userMessageGiftMapper.insert(userMessageGift);
    }
}
