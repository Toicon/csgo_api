package com.csgo.service.lottery;

import com.csgo.constants.CommonBizCode;
import com.csgo.constants.SystemConfigConstants;
import com.csgo.domain.Gift;
import com.csgo.domain.enums.AnchorOpenEnum;
import com.csgo.domain.plus.anchor.UserAnchorWhite;
import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.domain.plus.config.SystemConfigFacade;
import com.csgo.domain.plus.jackpot.JackpotBillRecord;
import com.csgo.domain.plus.lucky.CommissionRecord;
import com.csgo.domain.plus.lucky.LotteryDrawRecord;
import com.csgo.domain.plus.user.UserBalancePlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.user.UserPrizePlus;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.framework.exception.BizClientException;
import com.csgo.mapper.plus.config.SystemConfigMapper;
import com.csgo.mapper.plus.hee.UserAnchorWhiteMapper;
import com.csgo.mapper.plus.lottery.CommissionRecordMapper;
import com.csgo.modular.backpack.BackpackFromSourceConstant;
import com.csgo.modular.backpack.logic.UserMessageLogic;
import com.csgo.modular.giftwish.domain.UserGiftWishDO;
import com.csgo.modular.giftwish.service.UserGiftWishService;
import com.csgo.mq.MqMessage;
import com.csgo.mq.producer.LotteryMessageProducer;
import com.csgo.service.UserMessageItemRecordService;
import com.csgo.service.UserMessageRecordService;
import com.csgo.service.UserMessageService;
import com.csgo.service.gift.UserMessageGiftService;
import com.csgo.service.jackpot.BoxAnchorJackpotService;
import com.csgo.service.jackpot.BoxJackpotService;
import com.csgo.service.lottery.support.*;
import com.csgo.service.second.UserSecondRechargeDayService;
import com.csgo.service.user.UserBalanceService;
import com.csgo.service.user.UserPrizeRecentService;
import com.csgo.service.user.UserPrizeService;
import com.csgo.support.GlobalConstants;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.csgo.service.lottery.support.LotteryDrawConstants.COMMISSION_RATE;

/**
 * @author admin
 * @description 公式抽奖器
 */
@Slf4j
@Service
public class LuckyBoxDrawService {

    @Autowired
    private UserAnchorWhiteMapper userAnchorWhiteMapper;
    @Autowired
    private CommissionRecordMapper commissionRecordMapper;
    @Autowired
    private UserBalanceService userBalanceService;
    @Autowired
    private UserPrizeService userPrizeService;
    @Autowired
    private LotteryMessageProducer messageProducer;
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
    private BoxJackpotService boxJackpotService;
    @Autowired
    private BoxAnchorJackpotService boxAnchorJackpotService;
    @Autowired
    private UserPrizeRecentService userPrizeRecentService;
    @Autowired
    private UserGiftWishService userGiftWishService;
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    @Autowired
    private UserSecondRechargeDayService userSecondRechargeDayService;

    @Transactional
    public List<LuckyGiftProduct> draw(LuckyGift luckyGift, UserPlus player, SystemConfigFacade config, int num, DrawBoxContext drawBoxContext) {
        if (num <= 0) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        BigDecimal originTotalPrice = luckyGift.getPrice();
        BigDecimal cost = originTotalPrice.multiply(BigDecimal.valueOf(num));

        BigDecimal commissionRate = config.rate(COMMISSION_RATE);
        BigDecimal commissionPrice = originTotalPrice.multiply(commissionRate);
        BigDecimal actualPrice = originTotalPrice.subtract(commissionPrice);
        if (GlobalConstants.RETAIL_USER_FLAG == player.getFlag()) {
            if (drawBoxContext.isKeyMode()) {
                boxJackpotService.updateBoxJackpotKey(cost, player);
            } else {
                BigDecimal stock = cost.multiply(new BigDecimal(0.65)); //入库比例
                boxJackpotService.updateBoxJackpot(stock, player);
            }
        }
        luckyGift.setPrice(actualPrice);
        List<LuckyGiftProduct> giftProducts = new ArrayList<>();
        BigDecimal totalLuckyChange = BigDecimal.ZERO;
        List<LotteryDrawRecord> records = new ArrayList<>();

        LuckyBoxDrawerContext context = new LuckyBoxDrawerContext(luckyGift.getGift());
        for (int i = 0; i < num; i++) {
            context.setLoop(i + 1);

            LuckyBoxDrawer luckyBoxDrawer = new LuckyBoxDrawer(luckyGift, player, config, boxJackpotService, context);
            LotteryDrawResult result = luckyBoxDrawer.newDraw();
            records.add(result.getRecord());
            LuckyGiftProduct hitProduct = result.getHitProduct();
            BigDecimal luckyChange = actualPrice.subtract(hitProduct.getPrice());
            result.getRecord().setProfit(luckyChange.negate());
            totalLuckyChange = totalLuckyChange.add(luckyChange);
            player.setLucky(player.getLucky().add(luckyChange));
            giftProducts.add(hitProduct);
        }

        String balanceNumber = "";
        if (!drawBoxContext.isKeyMode()) {
            UserBalancePlus balance = userBalanceService.cost(player, luckyGift.getBalance(), luckyGift.getDiamondBalance());
            balanceNumber = balance.getBalanceNumber();
        }

        List<MqMessage> messages = new ArrayList<>();
        messages.add(new MqMessage(MqMessage.Category.LOTTERY, MqMessage.Type.LOG, JSON.toJSON(records)));
        if (GlobalConstants.RETAIL_USER_FLAG == player.getFlag()) {
            commission(player, commissionPrice.multiply(BigDecimal.valueOf(num)), balanceNumber);
            JackpotBillRecord record = new JackpotBillRecord();
            record.setBalanceNumber(balanceNumber);
            record.setIncome(totalLuckyChange);
            record.setCb(player.getName());
            record.setCt(new Date());
            messages.add(new MqMessage(MqMessage.Category.LOTTERY, MqMessage.Type.JACKPOT, JSON.toJSON(record)));
        }
        messageProducer.record(messages);
        return giftProducts;
    }

    /**
     * 测试账号
     *
     * @param luckyGift
     * @param player
     * @param config
     * @param num
     * @return
     */
    @Transactional
    public List<LuckyGiftProduct> drawAnchor(LuckyGift luckyGift, UserPlus player, SystemConfigFacade config, int num, DrawBoxContext drawBoxContext) {
        if (num <= 0) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        BigDecimal originTotalPrice = luckyGift.getPrice();
        BigDecimal cost = originTotalPrice.multiply(BigDecimal.valueOf(num));
        BigDecimal commissionRate = config.rate(COMMISSION_RATE);
        BigDecimal commissionPrice = originTotalPrice.multiply(commissionRate);
        BigDecimal actualPrice = originTotalPrice.subtract(commissionPrice);

        if (drawBoxContext.isKeyMode()) {
            boxAnchorJackpotService.updateBoxAnchorJackpotKey(cost, player);
        } else {
            BigDecimal stock = cost.multiply(new BigDecimal(0.8)); //入库比例
            boxAnchorJackpotService.updateBoxAnchorJackpot(stock, player);
        }

        luckyGift.setPrice(actualPrice);
        List<LuckyGiftProduct> giftProducts = new ArrayList<>();
        BigDecimal totalLuckyChange = BigDecimal.ZERO;
        List<LotteryDrawRecord> records = new ArrayList<>();
        LuckyBoxDrawerContext context = new LuckyBoxDrawerContext(luckyGift.getGift());
        for (int i = 0; i < num; i++) {
            context.setLoop(i + 1);
            LuckyBoxAnchorDrawer luckyBoxDrawer = new LuckyBoxAnchorDrawer(luckyGift, player, config, boxAnchorJackpotService, context);
            LotteryDrawResult result = luckyBoxDrawer.newDraw();
            records.add(result.getRecord());
            LuckyGiftProduct hitProduct = result.getHitProduct();
            BigDecimal luckyChange = actualPrice.subtract(hitProduct.getPrice());
            result.getRecord().setProfit(luckyChange.negate());
            totalLuckyChange = totalLuckyChange.add(luckyChange);
            player.setLucky(player.getLucky().add(luckyChange));
            giftProducts.add(hitProduct);
        }

        if (!drawBoxContext.isKeyMode()) {
            userBalanceService.cost(player, luckyGift.getBalance(), luckyGift.getDiamondBalance());
        }

        List<MqMessage> messages = new ArrayList<>();
        messages.add(new MqMessage(MqMessage.Category.LOTTERY, MqMessage.Type.LOG, JSON.toJSON(records)));
        messageProducer.record(messages);
        return giftProducts;
    }

    // 首页抽奖
    @Transactional
    public List<LuckyGiftProduct> draw(Gift gift, BigDecimal pay, LuckyGift luckyGift, UserPlus player, SystemConfigFacade config, int num, DrawBoxContext drawBoxContext) {
        List<LuckyGiftProduct> giftProducts;
        SystemConfig systemConfig = systemConfigMapper.get(SystemConfigConstants.ANCHOR_OPEN);
        //测试奖池开箱开关
        if (null == systemConfig || AnchorOpenEnum.NO.getCode().equals(systemConfig.getValue())) {
            //关闭状态
            //判断是否是白名单信息，如果是白名单信息则走测试
            List<UserAnchorWhite> userAnchorWhiteList = userAnchorWhiteMapper.findByUserId(player.getId());
            if (CollectionUtils.isEmpty(userAnchorWhiteList)) {
                //不是白名单，走原来开箱
                giftProducts = draw(luckyGift, player, config, num, drawBoxContext);
            } else {
                //是白名单，走测试账号
                giftProducts = drawAnchor(luckyGift, player, config, num, drawBoxContext);
            }
        } else {
            //打开状态
            if (GlobalConstants.RETAIL_USER_FLAG == player.getFlag()) {
                //散户
                giftProducts = draw(luckyGift, player, config, num, drawBoxContext);
            } else {
                //测试账号
                giftProducts = drawAnchor(luckyGift, player, config, num, drawBoxContext);
            }
        }
        Date current = new Date();
        for (LuckyGiftProduct giftProduct : giftProducts) {
            int userMessageId = record(player, gift, giftProduct, current);
            giftProduct.setUserMessageId(userMessageId);
        }
        //二次充值每日开箱累计
        Integer openCount = num;
        BigDecimal consumeAmount = gift.getPrice().multiply(BigDecimal.valueOf(num));
        userSecondRechargeDayService.save(player.getId(), openCount, consumeAmount);
        //开箱心愿
        //handleGiftWish(player, gift, pay, giftProducts);

        return giftProducts;
    }

    /**
     * 处理开箱心愿
     */
    private void handleGiftWish(UserPlus player, Gift gift, BigDecimal pay, List<LuckyGiftProduct> drawList) {
        if (!gift.getWishSwitch()) {
            return;
        }
        Integer userId = player.getId();

        try {
            Set<Integer> ownerGiftProductIds = drawList.stream().map(LuckyGiftProduct::getId).collect(Collectors.toSet());
            UserGiftWishDO entity = userGiftWishService.getExist(userId, gift.getId());
            if (entity != null) {
                for (LuckyGiftProduct item : drawList) {
                    item.setWishing(entity.getGiftProductId().equals(item.getId()));
                }
                userGiftWishService.addWishOrResetWish(entity, pay, ownerGiftProductIds);
            }
        } catch (Exception e) {
            log.error("心愿箱处理错误", e);
        }
    }

    private int record(UserPlus player, Gift gift, LuckyGiftProduct giftProduct, Date current) {
        //背包流水记录
        int recordId = userMessageRecordService.add(player.getId(), "BOX", "IN");
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
        message.setFromSource(BackpackFromSourceConstant.BOX);
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

        //增加用户的中奖记录
        UserPrizePlus userPrize = new UserPrizePlus();
        userPrize.setUserId(Integer.valueOf(player.getId() + ""));
        String userName = player.getName();
        userPrize.setUserNameQ(userName);
        String phoneNumber = userName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        userPrize.setUserName(phoneNumber);
        userPrize.setGiftId(Integer.valueOf(gift.getId() + ""));
        userPrize.setGiftProductId(giftProduct.getId());
        userPrize.setGiftProductName(giftProduct.getName());
        userPrize.setGiftType(gift.getType());
        userPrize.setGiftProductImg(giftProduct.getImg());
        userPrize.setPrice(giftProduct.getPrice());
        userPrize.setCsgoType(giftProduct.getCsgoType());
        userPrize.setGiftName(gift.getName());
        userPrize.setGiftGradeG(String.valueOf(giftProduct.getOutProbability()));
        userPrize.setGameName("CSGO");
        userPrize.setCt(current);
        userPrizeService.insert(userPrize);

        userPrizeRecentService.addRecentGiftBoxUserPrize(userPrize);

        giftProduct.setUserPrizeId(userPrize.getId());
        return message.getId();
    }

    //抽成记录
    private void commission(UserPlus user, BigDecimal price, String balanceNumber) {
        CommissionRecord record = new CommissionRecord();
        record.setBalanceNumber(balanceNumber);
        record.setUserId(user.getId());
        record.setPrice(price);
        record.setCt(new Date());
        commissionRecordMapper.insert(record);
    }
}
