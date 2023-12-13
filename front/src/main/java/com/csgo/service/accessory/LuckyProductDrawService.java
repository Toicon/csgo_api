package com.csgo.service.accessory;

import com.csgo.constants.LuckyProductConstants;
import com.csgo.domain.plus.accessory.LuckyProductDTO;
import com.csgo.domain.plus.config.SystemConfigFacade;
import com.csgo.domain.plus.gift.RandomGiftProductDTO;
import com.csgo.domain.plus.jackpot.LuckyProductJackpotBillRecord;
import com.csgo.domain.plus.user.UserBalancePlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.user.UserPrizePlus;
import com.csgo.domain.user.UserLuckyHistory;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.mapper.UserMessageGiftMapper;
import com.csgo.mapper.plus.user.UserBalancePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.backpack.BackpackFromSourceConstant;
import com.csgo.modular.backpack.logic.UserMessageLogic;
import com.csgo.mq.MqMessage;
import com.csgo.mq.producer.LuckyProductLotteryMessageProducer;
import com.csgo.service.UserLuckyHistoryService;
import com.csgo.service.UserMessageItemRecordService;
import com.csgo.service.UserMessageRecordService;
import com.csgo.service.UserMessageService;
import com.csgo.service.accessory.support.LuckyProductDrawResult;
import com.csgo.service.accessory.support.LuckyProductDrawer;
import com.csgo.service.accessory.support.LuckyProductDrawerCondition;
import com.csgo.service.config.SystemConfigService;
import com.csgo.service.jackpot.JackpotService;
import com.csgo.service.jackpot.UpgradeJackpotService;
import com.csgo.service.user.UserPrizeService;
import com.csgo.support.GlobalConstants;
import com.csgo.util.StringUtil;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class LuckyProductDrawService {
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private UserBalancePlusMapper userBalancePlusMapper;
    @Autowired
    private UserMessageGiftMapper messageMapper;
    @Autowired
    private UserPrizeService userPrizeService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private JackpotService jackpotService;
    @Autowired
    private UserLuckyHistoryService userLuckyHistoryService;
    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private UserMessageLogic userMessageLogic;
    @Autowired
    private UserMessageItemRecordService userMessageItemRecordService;
    @Autowired
    private LuckyProductLotteryMessageProducer messageProducer;
    @Autowired
    private UpgradeJackpotService upgradeJackpotService;

    @Transactional
    public UserLuckyHistory draw(LuckyProductDrawerCondition condition) {
        BigDecimal pay = condition.getPay();
        UserPlus player = condition.getPlayer();
        LuckyProductDTO luckyProduct = condition.getLuckyProduct();
        SystemConfigFacade config = systemConfigService.findByPrefix(LuckyProductConstants.LUCKY_PRODUCT_PREFIX);

        BigDecimal commissionRate = config.rate(LuckyProductConstants.COMMISSION_RATE);
        BigDecimal commissionPrice = pay.multiply(commissionRate);
        BigDecimal actualPrice = pay.subtract(commissionPrice);
        LuckyProductDrawer luckyProductDrawer = new LuckyProductDrawer(condition, jackpotService, upgradeJackpotService, config);
        LuckyProductDrawResult result;
        if (GlobalConstants.RETAIL_USER_FLAG == player.getFlag()) {
            BigDecimal stock = pay.multiply(new BigDecimal(0.65)); //入库比例
            upgradeJackpotService.updateUpgradeJackpot(stock, player);
        }
        result = luckyProductDrawer.draw();
        UserLuckyHistory userLuckyHistory = new UserLuckyHistory();
        userLuckyHistory.setAddTime(new Date());
        userLuckyHistory.setUserId(player.getId());
        userLuckyHistory.setLuckNumber(StringUtil.randomNumber(18));
        userLuckyHistory.setPrice(condition.getPay());
        userLuckyHistory.setLuckyId(condition.getLuckyId());
        userLuckyHistory.setProbability(condition.getLuckyValue().intValue());
        setProductInfo(result, userLuckyHistory, luckyProduct, condition.getRandomGiftProductDTO(), player);
        userLuckyHistoryService.add(userLuckyHistory);
        BigDecimal income = result.isHit() ? luckyProduct.getPrice().subtract(actualPrice).negate() : actualPrice.subtract(userLuckyHistory.getRandomProductPrice());
        result.getRecord().setProfit(income.negate());
        player.setAccessoryLucky(player.getAccessoryLucky().add(income));
        UserBalancePlus balance = cost(player, condition.getBalance(), condition.getDiamondBalance());
        addMessage(userLuckyHistory, player);
        List<MqMessage> messages = new ArrayList<>();
        messages.add(new MqMessage(MqMessage.Category.LUCKY_PRODUCT, MqMessage.Type.LOG, JSON.toJSON(result.getRecord())));
        if (GlobalConstants.RETAIL_USER_FLAG == player.getFlag()) {
            LuckyProductJackpotBillRecord record = new LuckyProductJackpotBillRecord();
            record.setBalanceNumber(balance.getBalanceNumber());
            record.setIncome(income);
            record.setCb(String.valueOf(player.getId()));
            record.setCt(new Date());
            messages.add(new MqMessage(MqMessage.Category.LUCKY_PRODUCT, MqMessage.Type.JACKPOT, JSON.toJSON(record)));
        }
        messageProducer.record(messages);
        return userLuckyHistory;
    }

    private void setProductInfo(LuckyProductDrawResult result, UserLuckyHistory userLuckyHistory, LuckyProductDTO luckyProduct, RandomGiftProductDTO randomGiftProductDTO, UserPlus player) {
        if (result.isHit()) {
            userLuckyHistory.setLuckyProductId(luckyProduct.getGiftProductId());
            userLuckyHistory.setLuckyProductName(luckyProduct.getProductName());
            userLuckyHistory.setLuckyProductImg(luckyProduct.getImgUrl());
            userLuckyHistory.setLuckyProductPrice(luckyProduct.getPrice());
            userLuckyHistory.setIsLucky(1);
            result.getRecord().setHitProductId(luckyProduct.getGiftProductId());
            return;
        }
        if (randomGiftProductDTO != null) {
            log.info("抽中的随机饰品为：" + randomGiftProductDTO.toString());
            userLuckyHistory.setRandomProductId(randomGiftProductDTO.getGiftProductId());
            userLuckyHistory.setRandomProductName(randomGiftProductDTO.getGiftProductName());
            userLuckyHistory.setRandomProductImg(randomGiftProductDTO.getGiftProductImg());
            userLuckyHistory.setRandomProductPrice(randomGiftProductDTO.getGiftProductPrice());
            result.getRecord().setHitProductId(randomGiftProductDTO.getGiftProductId());
            if (GlobalConstants.RETAIL_USER_FLAG == player.getFlag()) {
                upgradeJackpotService.updateUpgradeJackpot(randomGiftProductDTO.getGiftProductPrice().multiply(new BigDecimal(-1)), player);
            }
        }
    }

    private void addMessage(UserLuckyHistory userLuckyHistory, UserPlus user) {

        //背包流水记录
        int recordId = userMessageRecordService.add(user.getId(), "Upgrade", "IN");

        if (userLuckyHistory.getRandomProductId() != null) {
            //增加用户背包信息
            UserMessagePlus message = new UserMessagePlus();
            message.setGameName("Upgrade");
            message.setGiftProductId(userLuckyHistory.getRandomProductId());
            message.setGiftType("Upgrade");
            message.setMoney(userLuckyHistory.getRandomProductPrice());
            message.setDrawDare(new Date());
            message.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
            message.setImg(userLuckyHistory.getRandomProductImg());
            message.setUserId(userLuckyHistory.getUserId());
            message.setProductName(userLuckyHistory.getRandomProductName());
            message.setGiftStatus("0");
            message.setFromSource(BackpackFromSourceConstant.LUCKY);
            userMessageLogic.simpleProcessGiftKeyProduct(message, userLuckyHistory.getRandomProductName());
            userMessageService.insert(message);
            userLuckyHistory.setRandomMessageId(message.getId());

            //背包流水详情记录
            userMessageItemRecordService.add(recordId, message.getId(), message.getImg());
            //抽奖入库记录
            UserMessageGift userMessageGift = new UserMessageGift();
            userMessageGift.setCt(new Date());
            userMessageGift.setUserMessageId(message.getId());
            userMessageGift.setGiftProductId(userLuckyHistory.getRandomProductId());
            userMessageGift.setUserId(userLuckyHistory.getUserId());
            userMessageGift.setImg(userLuckyHistory.getRandomProductImg());
            userMessageGift.setPhone(user.getUserName());
            userMessageGift.setState(0);
            userMessageGift.setMoney(userLuckyHistory.getRandomProductPrice());
            userMessageGift.setUt(new Date());
            userMessageGift.setGameName("Upgrade");
            userMessageGift.setGiftProductName(userLuckyHistory.getRandomProductName());
            userMessageGift.setGiftType("Upgrade");
            messageMapper.insert(userMessageGift);

            //增加用户的中奖记录
            UserPrizePlus userPrize = new UserPrizePlus();
            userPrize.setUserId(Integer.valueOf(user.getId() + ""));
            String userName = user.getName();
            userPrize.setUserNameQ(userName);
            userPrize.setUserName(userName);
            userPrize.setGameName("Upgrade");
            userPrize.setGiftId(0);
            userPrize.setGiftProductId(userLuckyHistory.getRandomProductId());
            userPrize.setGiftProductName(userLuckyHistory.getRandomProductName());
            userPrize.setGiftType("Upgrade");
            userPrize.setGiftProductImg(userLuckyHistory.getRandomProductImg());
            userPrize.setPrice(userLuckyHistory.getRandomProductPrice());
            userPrize.setGiftName("Upgrade");
            userPrizeService.insert(userPrize);
        }

        if (userLuckyHistory.getLuckyProductId() != null) {
            //增加用户背包信息
            UserMessagePlus message = new UserMessagePlus();
            message.setGameName("Upgrade");
            message.setGiftProductId(userLuckyHistory.getLuckyProductId());
            message.setGiftType("Upgrade");
            message.setMoney(userLuckyHistory.getLuckyProductPrice());
            message.setDrawDare(new Date());
            message.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
            message.setImg(userLuckyHistory.getLuckyProductImg());
            message.setUserId(userLuckyHistory.getUserId());
            message.setProductName(userLuckyHistory.getLuckyProductName());
            message.setGiftStatus("0");
            message.setFromSource(BackpackFromSourceConstant.LUCKY);
            userMessageLogic.simpleProcessGiftKeyProduct(message, userLuckyHistory.getLuckyProductName());
            userMessageService.insert(message);
            userLuckyHistory.setLuckyMessageId(message.getId());

            //背包流水详情记录
            userMessageItemRecordService.add(recordId, message.getId(), message.getImg());

            //抽奖入库记录
            UserMessageGift userMessageGift = new UserMessageGift();
            userMessageGift.setCt(new Date());
            userMessageGift.setUserMessageId(message.getId());
            userMessageGift.setGiftProductId(userLuckyHistory.getLuckyProductId());
            userMessageGift.setUserId(userLuckyHistory.getUserId());
            userMessageGift.setImg(userLuckyHistory.getLuckyProductImg());
            userMessageGift.setPhone(user.getUserName());
            userMessageGift.setState(0);
            userMessageGift.setMoney(userLuckyHistory.getLuckyProductPrice());
            userMessageGift.setUt(new Date());
            userMessageGift.setGameName("Upgrade");
            userMessageGift.setGiftProductName(userLuckyHistory.getLuckyProductName());
            userMessageGift.setGiftType("Upgrade");
            messageMapper.insert(userMessageGift);

            //增加用户的中奖记录
            UserPrizePlus userPrize = new UserPrizePlus();
            userPrize.setUserId(Integer.valueOf(user.getId() + ""));
            String userName = user.getName();
            userPrize.setUserNameQ(userName);
            if (!StringUtils.isEmpty(userName) && userName.length() == 11) {
                String phoneNumber = userName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                userPrize.setUserName(phoneNumber);
            }

            userPrize.setGameName("Upgrade");
            userPrize.setGiftId(0);
            userPrize.setGiftProductId(userLuckyHistory.getLuckyProductId());
            userPrize.setGiftProductName(userLuckyHistory.getLuckyProductName());
            userPrize.setGiftType("Upgrade");
            userPrize.setGiftProductImg(userLuckyHistory.getLuckyProductImg());
            userPrize.setPrice(userLuckyHistory.getLuckyProductPrice());
            userPrize.setGiftName("Upgrade");
            userPrizeService.insert(userPrize);
        }
    }

    private UserBalancePlus cost(UserPlus user, BigDecimal balance, BigDecimal diamondBalance) {
        user.setBalance(user.getBalance().subtract(balance));
        userPlusMapper.updateById(user);

        UserBalancePlus userBalance = new UserBalancePlus();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(balance);
        userBalance.setDiamondAmount(diamondBalance);
        userBalance.setType(2); //支出
        userBalance.setRemark("幸运宝箱");
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setCurrentDiamondAmount(user.getDiamondBalance());
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalancePlusMapper.insert(userBalance);
        return userBalance;
    }
}
