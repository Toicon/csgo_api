package com.csgo.service.fish;

import com.csgo.constants.LuckyProductConstants;
import com.csgo.domain.Gift;
import com.csgo.domain.plus.config.SystemConfigFacade;
import com.csgo.domain.plus.lucky.LotteryDrawRecord;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.modular.backpack.logic.UserMessageLogic;
import com.csgo.service.jackpot.FishAnchorJackpotService;
import com.csgo.service.jackpot.FishUserJackpotService;
import com.csgo.service.message.UserMessageGiftService;
import com.csgo.service.message.UserMessageItemRecordService;
import com.csgo.service.message.UserMessageRecordService;
import com.csgo.service.message.UserMessageService;
import com.csgo.support.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class FishDrawService {
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private UserMessageLogic userMessageLogic;
    @Autowired
    private UserMessageGiftService userMessageGiftService;
    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private UserMessageItemRecordService userMessageItemRecordService;
    @Autowired
    private FishUserJackpotService fishUserJackpotService;
    @Autowired
    private FishAnchorJackpotService fishAnchorJackpotService;

    @Transactional
    public LuckyGiftProduct draw(LuckyGift luckyGift, UserPlus player, SystemConfigFacade config, int num) {
        BigDecimal originTotalPrice = luckyGift.getPrice();
        BigDecimal commissionRate = config.rate(LuckyProductConstants.COMMISSION_RATE);
        BigDecimal commissionPrice = originTotalPrice.multiply(commissionRate);
        BigDecimal actualPrice = originTotalPrice.subtract(commissionPrice);
        luckyGift.setPrice(actualPrice);
        List<LuckyGiftProduct> giftProducts = new ArrayList<>();
        BigDecimal totalLuckyChange = BigDecimal.ZERO;
        List<LotteryDrawRecord> records = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            FishUserDrawer luckyBoxDrawer = new FishUserDrawer(luckyGift, player, config, fishUserJackpotService);
            LotteryDrawResult result = luckyBoxDrawer.newDraw();
            records.add(result.getRecord());
            LuckyGiftProduct hitProduct = result.getHitProduct();
            BigDecimal luckyChange = actualPrice.subtract(hitProduct.getPrice());
            result.getRecord().setProfit(luckyChange.negate());
            totalLuckyChange = totalLuckyChange.add(luckyChange);
            player.setLucky(player.getLucky().add(luckyChange));
            giftProducts.add(hitProduct);
        }
        LuckyGiftProduct luckyGiftProduct = giftProducts.stream().max(Comparator.comparing(LuckyGiftProduct::getPrice)).orElse(null);
        if (luckyGiftProduct != null) {
            fishUserJackpotService.updateFishUserJackpot(luckyGiftProduct.getPrice().multiply(new BigDecimal(-1)), player);
        }
        return luckyGiftProduct;
    }

    @Transactional
    public LuckyGiftProduct drawAnchor(LuckyGift luckyGift, UserPlus player, SystemConfigFacade config, int num) {
        BigDecimal originTotalPrice = luckyGift.getPrice();
        BigDecimal commissionRate = config.rate(LuckyProductConstants.COMMISSION_RATE);
        BigDecimal commissionPrice = originTotalPrice.multiply(commissionRate);
        BigDecimal actualPrice = originTotalPrice.subtract(commissionPrice);
        luckyGift.setPrice(actualPrice);
        List<LuckyGiftProduct> giftProducts = new ArrayList<>();
        BigDecimal totalLuckyChange = BigDecimal.ZERO;
        List<LotteryDrawRecord> records = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            FishAnchorDrawer luckyBoxDrawer = new FishAnchorDrawer(luckyGift, player, config, fishAnchorJackpotService);
            LotteryDrawResult result = luckyBoxDrawer.newDraw();
            records.add(result.getRecord());
            LuckyGiftProduct hitProduct = result.getHitProduct();
            BigDecimal luckyChange = actualPrice.subtract(hitProduct.getPrice());
            result.getRecord().setProfit(luckyChange.negate());
            totalLuckyChange = totalLuckyChange.add(luckyChange);
            player.setLucky(player.getLucky().add(luckyChange));
            giftProducts.add(hitProduct);
        }
        LuckyGiftProduct luckyGiftProduct = giftProducts.stream().max(Comparator.comparing(LuckyGiftProduct::getPrice)).orElse(null);
        if (luckyGiftProduct != null) {
            fishAnchorJackpotService.updateFishAnchorJackpot(luckyGiftProduct.getPrice().multiply(new BigDecimal(-1)), player);
        }
        return luckyGiftProduct;
    }

    // 首页抽奖
    @Transactional
    public LuckyGiftProduct draw(Gift gift, LuckyGift luckyGift, UserPlus player, SystemConfigFacade config, int num) {
        LuckyGiftProduct giftProduct;
        if (GlobalConstants.RETAIL_USER_FLAG == player.getFlag()) {
            //散户
            giftProduct = draw(luckyGift, player, config, num);
        } else {
            //测试账号
            giftProduct = drawAnchor(luckyGift, player, config, num);
        }
        int userMessageId = record(player, gift, giftProduct);
        giftProduct.setUserMessageId(userMessageId);
        return giftProduct;
    }

    private int record(UserPlus player, Gift gift, LuckyGiftProduct giftProduct) {
        //背包流水记录
        int recordId = userMessageRecordService.add(player.getId(), "钓鱼玩法", "IN");
        //增加用户背包信息
        UserMessagePlus message = new UserMessagePlus();
        message.setGameName(giftProduct.getGameName());
        message.setGiftProductId(giftProduct.getId());
        message.setMoney(giftProduct.getPrice());
        message.setDrawDare(new Date());
        message.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
        message.setGameName("CSGO");
        message.setImg(giftProduct.getImg());
        message.setGiftType(gift.getType());
        message.setUserId(player.getId());
        message.setProductName(giftProduct.getName());
        message.setGiftStatus("0");
        userMessageLogic.processGiftKeyProduct(message, giftProduct.getCsgoType());
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
        userMessageGift.setGameName(giftProduct.getGameName());
        userMessageGift.setGiftProductName(giftProduct.getName());
        userMessageGift.setGiftType(gift.getType());
        userMessageGiftService.insert(userMessageGift);
        return message.getId();
    }
}
