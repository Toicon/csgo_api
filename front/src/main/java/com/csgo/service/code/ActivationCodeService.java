package com.csgo.service.code;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.code.ActivationCode;
import com.csgo.domain.plus.code.ProductType;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.mapper.plus.code.ActivationCodeMapper;
import com.csgo.modular.backpack.logic.UserMessageLogic;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.GiftProductService;
import com.csgo.service.UserMessageItemRecordService;
import com.csgo.service.UserMessageRecordService;
import com.csgo.service.UserMessageService;
import com.csgo.service.gift.UserMessageGiftService;
import com.csgo.service.user.UserBalanceService;
import com.csgo.support.GlobalConstants;
import com.csgo.web.response.code.ActivationCodeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Service
public class ActivationCodeService {

    @Autowired
    private RedisTemplateFacde templateFacde;

    @Autowired
    private ActivationCodeMapper activationCodeMapper;

    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private UserMessageLogic userMessageLogic;
    @Autowired
    private UserMessageItemRecordService userMessageItemRecordService;
    @Autowired
    private UserMessageGiftService userMessageGiftService;


    public List<ActivationCode> findByCdKey(String cdKey) {
        return activationCodeMapper.findByCdKey(cdKey);
    }

    @Transactional
    public ActivationCodeResult receive(UserPlus userPlus, ActivationCode code) {
        code.setUserId(userPlus.getId());
        code.setUserName(userPlus.getUserName());
        code.setFlag(userPlus.getFlag());
        code.setReceiveDate(new Date());
        BaseEntity.updated(code, userPlus.getUserName());
        activationCodeMapper.updateById(code);
        ActivationCodeResult result = new ActivationCodeResult();
        if (ProductType.PRODUCT.equals(code.getProductType())) {
            GiftProductPlus giftProductPlus = giftProductService.getPlus(code.getGiftProductId());
            result.setUserMessageId(record(userPlus, giftProductPlus, code));
            result.setGrade(giftProductPlus.getGrade());
            result.setProductImg(giftProductPlus.getImg());
            result.setProductName(giftProductPlus.getName());
        } else {
            userBalanceService.add(userPlus, code.getPrice(), "CDK领取", 1);
            result.setGrade("#8847ff");
            result.setProductImg("https://xxxxskins.oss-cn-shenzhen.aliyuncs.com/vb.png");
            result.setProductName("V币");
        }
        result.setCost(code.getPrice());
        result.setProductType(code.getProductType());
        return result;
    }

    private int record(UserPlus player, GiftProductPlus giftProduct, ActivationCode code) {
        //背包流水记录
        int recordId = userMessageRecordService.add(player.getId(), "CDK领取", "IN");
        //增加用户背包信息
        UserMessagePlus message = new UserMessagePlus();
        message.setGameName("CDK领取");
        message.setGiftProductId(giftProduct.getId());
        message.setMoney(code.getPrice());
        message.setDrawDare(new Date());
        message.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
        message.setGameName("CDK领取");
        message.setImg(giftProduct.getImg());
        message.setGiftType("CDK领取");
        message.setUserId(player.getId());
        message.setProductName(giftProduct.getName());
        message.setGiftStatus("0");
        userMessageLogic.commonProcess(message, giftProduct);
        userMessageService.insert(message);

        //背包流水详情记录
        userMessageItemRecordService.add(recordId, message.getId(), message.getImg());

        //抽奖入库记录
        UserMessageGift userMessageGift = new UserMessageGift();
        userMessageGift.setCt(new Date());
        userMessageGift.setUserMessageId(message.getId());
        userMessageGift.setGiftProductId(message.getGiftProductId());
        userMessageGift.setUserId(player.getId());
        userMessageGift.setImg(message.getImg());
        userMessageGift.setPhone(player.getUserName());
        userMessageGift.setState(0);
        userMessageGift.setMoney(message.getMoney());
        userMessageGift.setUt(new Date());
        userMessageGift.setGameName("CDK领取");
        userMessageGift.setGiftProductName(giftProduct.getName());
        userMessageGift.setGiftType("CDK领取");
        userMessageGiftService.insert(userMessageGift);
        return message.getId();
    }
}
