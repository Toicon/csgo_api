package com.csgo.service.shop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchShopProductCondition;
import com.csgo.condition.shop.SearchUserCurrencyExchangeCondition;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.shop.Shop;
import com.csgo.domain.plus.shop.ShopCurrencyConfig;
import com.csgo.domain.plus.shop.ShopGiftProductDTO;
import com.csgo.domain.plus.shop.ShopOrder;
import com.csgo.domain.plus.user.UserBalancePlus;
import com.csgo.domain.plus.user.UserCurrencyExchange;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.framework.exception.BizClientException;
import com.csgo.mapper.UserMessageGiftMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.shop.ShopCurrencyConfigMapper;
import com.csgo.mapper.plus.shop.ShopMapper;
import com.csgo.mapper.plus.shop.ShopOrderMapper;
import com.csgo.mapper.plus.shop.UserCurrencyExchangeMapper;
import com.csgo.mapper.plus.user.UserBalancePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.backpack.logic.UserMessageLogic;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.UserMessageItemRecordService;
import com.csgo.service.UserMessageRecordService;
import com.csgo.service.UserMessageService;
import com.csgo.support.ConcurrencyLimit;
import com.csgo.support.GlobalConstants;
import com.csgo.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Service
public class ShopService {

    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private UserBalancePlusMapper userBalanceMapper;
    @Autowired
    private ShopOrderMapper shopOrderMapper;
    @Autowired
    private UserMessageGiftMapper messageMapper;
    @Autowired
    private ShopCurrencyConfigMapper shopCurrencyConfigMapper;
    @Autowired
    private UserCurrencyExchangeMapper userCurrencyExchangeMapper;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private UserMessageLogic userMessageLogic;
    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private UserMessageItemRecordService userMessageItemRecordService;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;

    public Page<ShopGiftProductDTO> pagination(SearchShopProductCondition condition) {
        return giftProductPlusMapper.pagination(condition.getPage(), condition);
    }

    public void initStock() {
        List<Shop> shops = shopMapper.selectList(null);
        for (Shop shop : shops) {
            if (null != shop.getHidden() && !shop.getHidden()) {
                redisTemplateFacde.getAndSet("SHOP_" + shop.getGiftProductId(), String.valueOf(shop.getStock()));
            }
        }
    }

    @Transactional
    public int buy(int userId, int giftProductId) {
        GiftProductPlus giftProduct = giftProductPlusMapper.selectById(giftProductId);
        UserPlus user = userPlusMapper.selectById(userId);
        if (user.getDiamondBalance().compareTo(giftProduct.getPrice()) < 0) {
            throw BizClientException.of(CommonBizCode.USER_DIAMOND_LACK);
        }

//        int stock = reduceStock(giftProductId);
        Shop shop = shopMapper.get(giftProductId);
        if (null == shop.getStock() || shop.getStock() <= 0) {
            throw BizClientException.of(CommonBizCode.SHOP_STOCK_LACK);
        }
        shop.setStock(shop.getStock() - 1);
        shop.setUt(new Date());
        shop.setUb(user.getUserName());
        shopMapper.updateById(shop);
        cost(user, giftProduct);
        record(user, giftProduct);
        return shop.getStock();
    }

    /**
     * 商城货币兑换
     *
     * @param userId
     * @param configId
     */
    @Transactional
    @ConcurrencyLimit
    public void exchange(int userId, Integer configId) {
        ShopCurrencyConfig shopCurrencyConfig = shopCurrencyConfigMapper.selectById(configId);
        if (shopCurrencyConfig == null) {
            throw BizClientException.of(CommonBizCode.COMMON_DATA_NOT_FOUND);
        }
        UserPlus user = userPlusMapper.selectById(userId);
        if (user == null) {
            throw BizClientException.of(CommonBizCode.USER_NOT_FOUND);
        }
        if (user.getBalance().compareTo(shopCurrencyConfig.getDiamondAmount()) < 0) {
            throw BizClientException.of(CommonBizCode.USER_DIAMOND_LACK);
        }
        //兑换金额(银币)
        BigDecimal diamondAmount = shopCurrencyConfig.getDiamondAmount();
        //赠送金额(银币)
        BigDecimal giveAmount = diamondAmount.multiply(shopCurrencyConfig.getGiveRate()).divide(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP));
        BigDecimal exchangeAmount = diamondAmount.add(giveAmount);
        //记录兑换流水记录
        UserCurrencyExchange userCurrencyExchange = new UserCurrencyExchange();
        userCurrencyExchange.setUserId(userId);
        userCurrencyExchange.setDiamondAmount(diamondAmount);
        userCurrencyExchange.setGiveAmount(giveAmount);
        userCurrencyExchange.setFrontBalance(user.getBalance());
        userCurrencyExchange.setBalance(user.getBalance().subtract(diamondAmount));
        userCurrencyExchange.setCreateDate(new Date());
        userCurrencyExchangeMapper.insert(userCurrencyExchange);
        //修改用户余额信息
        user.setBalance(user.getBalance().subtract(diamondAmount));
        user.setDiamondBalance(user.getDiamondBalance().add(exchangeAmount));
        user.setUpdatedAt(new Date());
        userPlusMapper.updateById(user);
        //增加兑换流水
        this.currencyExchangeCost(user, diamondAmount, exchangeAmount);
    }

    /**
     * 获取兑换货币列表
     *
     * @return
     */
    public List<ShopCurrencyConfig> findShopCurrencyConfigList() {
        return shopCurrencyConfigMapper.findAll();
    }

    /**
     * 商城货币兑换记录
     *
     * @return
     */
    public Page<UserCurrencyExchange> findUserCurrencyExchangeList(SearchUserCurrencyExchangeCondition condition) {
        return userCurrencyExchangeMapper.pagination(condition);
    }


    private UserBalancePlus cost(UserPlus user, GiftProductPlus giftProduct) {
        ShopOrder order = new ShopOrder();
        order.setUserId(user.getId());
        order.setGiftProductId(giftProduct.getId());
        order.setCt(new Date());
        order.setBeforeBalance(user.getDiamondBalance());
        order.setAfterBalance(user.getDiamondBalance().subtract(giftProduct.getPrice()));
        shopOrderMapper.insert(order);

        user.setDiamondBalance(user.getDiamondBalance().subtract(giftProduct.getPrice()));
        userPlusMapper.updateById(user);

        UserBalancePlus userBalance = new UserBalancePlus();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(BigDecimal.ZERO);
        userBalance.setDiamondAmount(giftProduct.getPrice());
        userBalance.setType(2); //支出
        userBalance.setRemark("商城兑换");
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setCurrentDiamondAmount(user.getDiamondBalance());
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalanceMapper.insert(userBalance);
        return userBalance;
    }

    private int record(UserPlus user, GiftProductPlus giftProduct) {
        //背包流水记录
        int recordId = userMessageRecordService.add(user.getId(), "商城购买", "IN");
        //增加用户背包信息
        UserMessagePlus message = new UserMessagePlus();
        message.setGameName(giftProduct.getGameName());
        message.setGiftProductId(giftProduct.getId());
        message.setMoney(giftProduct.getPrice());
        message.setDrawDare(new Date());
        message.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
        message.setGameName("Shop");
        message.setImg(giftProduct.getImg());
        message.setGiftType("Shop");
        message.setUserId(user.getId());
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
        userMessageGift.setGiftProductId(giftProduct.getId());
        userMessageGift.setUserId(user.getId());
        userMessageGift.setImg(giftProduct.getImg());
        userMessageGift.setPhone(user.getUserName());
        userMessageGift.setState(0);
        userMessageGift.setMoney(giftProduct.getPrice());
        userMessageGift.setUt(new Date());
        userMessageGift.setGameName("Shop");
        userMessageGift.setGiftProductName(giftProduct.getName());
        userMessageGift.setGiftType("Shop");
        messageMapper.insert(userMessageGift);
        return message.getId();
    }

    public void currencyExchangeCost(UserPlus user, BigDecimal balance, BigDecimal diamondBalance) {

        UserBalancePlus userBalance = new UserBalancePlus();
        userBalance.setAddTime(new Date());
        userBalance.setType(2); //支出
        userBalance.setRemark("商城兑换");
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalance.setAmount(balance);
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setDiamondAmount(BigDecimal.ZERO);
        userBalance.setCurrentDiamondAmount(user.getDiamondBalance().subtract(diamondBalance));
        userBalanceMapper.insert(userBalance);


        UserBalancePlus userBalanceAdd = new UserBalancePlus();
        userBalanceAdd.setAddTime(new Date());
        userBalanceAdd.setType(1); //收入
        userBalanceAdd.setRemark("商城兑换");
        userBalanceAdd.setUserId(user.getId());
        userBalanceAdd.setBalanceNumber(StringUtil.randomNumber(15));
        userBalanceAdd.setAmount(BigDecimal.ZERO);
        userBalanceAdd.setCurrentAmount(user.getBalance());
        userBalanceAdd.setDiamondAmount(diamondBalance);
        userBalanceAdd.setCurrentDiamondAmount(user.getDiamondBalance());
        userBalanceMapper.insert(userBalanceAdd);
    }
}
