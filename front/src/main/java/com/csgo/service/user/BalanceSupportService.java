package com.csgo.service.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csgo.constants.CommonBizCode;
import com.csgo.constants.SystemConstant;
import com.csgo.domain.Gift;
import com.csgo.domain.plus.accessory.LuckyProductDTO;
import com.csgo.domain.plus.code.ActivationCode;
import com.csgo.domain.plus.config.SystemConfigFacade;
import com.csgo.domain.plus.fish.FishConfig;
import com.csgo.domain.plus.gift.GiftProductRecordPlus;
import com.csgo.domain.plus.gift.RandomGiftProductDTO;
import com.csgo.domain.plus.user.UserMessageGiftPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserBalance;
import com.csgo.domain.user.UserLuckyHistory;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.exception.BizServerException;
import com.csgo.mapper.plus.fish.FishConfigMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.modular.backpack.service.UserBackpackService;
import com.csgo.modular.product.enums.ProductKindEnums;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.GiftProductService;
import com.csgo.service.UserMessageRecordService;
import com.csgo.service.UserMessageService;
import com.csgo.service.accessory.LuckyProductDrawService;
import com.csgo.service.accessory.LuckyProductService;
import com.csgo.service.accessory.support.LuckyProductDrawerCondition;
import com.csgo.service.code.ActivationCodeService;
import com.csgo.service.config.SystemConfigService;
import com.csgo.service.gift.GiftProductRecordService;
import com.csgo.service.gift.UserMessageGiftService;
import com.csgo.service.lock.RedissonLockService;
import com.csgo.service.lottery.KeyBoxDrawService;
import com.csgo.service.lottery.LuckyBoxDrawService;
import com.csgo.service.lottery.support.DrawBoxContext;
import com.csgo.service.lottery.support.LuckyGift;
import com.csgo.service.lottery.support.LuckyGiftProduct;
import com.csgo.service.shop.ShopService;
import com.csgo.service.withdraw.WithdrawPropZbtService;
import com.csgo.support.ConcurrencyLimit;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.StringUtil;
import com.csgo.web.request.accessory.LuckyProductRequest;
import com.csgo.web.request.gift.LotteryDrawRequest;
import com.csgo.web.request.gift.SellProductRequest;
import com.csgo.web.request.withdraw.WithdrawPropRequest;
import com.csgo.web.response.code.ActivationCodeResult;
import com.csgo.web.response.gift.ShopGiftProductResponse;
import com.csgo.web.response.lucky.UserLuckyHistoryResponse;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.csgo.service.lottery.support.LotteryDrawConstants.*;

/**
 * @author admin
 */
@Slf4j
@Service
public class BalanceSupportService {
    private static final String LUCKY_PRODUCT_LOTTERY = "LUCKY_PRODUCT_LOTTERY_";
    private static final String GIFT_PRODUCT_LOTTERY = "GIFT_PRODUCT_LOTTERY_";

    @Autowired
    private UserService userService;
    @Autowired
    private LuckyProductService luckyProductService;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private LuckyProductDrawService luckyProductDrawService;
    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private GiftProductRecordService giftProductRecordService;
    @Autowired
    private LuckyBoxDrawService luckyBoxDrawService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private UserMessageGiftService userMessageGiftService;
    @Autowired
    private UserBalanceService userBalanceService;
    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private WithdrawPropZbtService withdrawPropService;
    @Autowired
    private ActivationCodeService activationCodeService;
    @Autowired
    private UserPrizeService userPrizeService;

    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;

    @Autowired
    private UserBackpackService userBackpackService;
    @Autowired
    private RedissonLockService redissonLockService;
    @Autowired
    private KeyBoxDrawService keyBoxDrawService;

    @Autowired
    private FishConfigMapper fishConfigMapper;

    @ConcurrencyLimit
    public UserLuckyHistoryResponse luckyProduct(int userId, LuckyProductRequest request, BigDecimal pay, LuckyProductDTO luckyProduct) {
        UserPlus player = userService.get(userId);
        BigDecimal balance = pay;
        BigDecimal diamondBalance = BigDecimal.ZERO;
        if (player.getBalance().compareTo(pay) < 0) {
            throw BizClientException.of(CommonBizCode.USER_BALANCE_LACK);
        }
        //获取价格范围随机饰品
        RandomGiftProductDTO randomGiftProductDTO = this.getRandomProduct();
        if (randomGiftProductDTO == null) {
            throw BizClientException.of(CommonBizCode.SYS_ERROR_LUCKY_RANDOM);
        }
        String key = LUCKY_PRODUCT_LOTTERY + userId;
        if (!StringUtils.isEmpty(redisTemplateFacde.get(key))) {
            throw BizClientException.of(CommonBizCode.COMMON_BUSY);
        }
        redisTemplateFacde.set(key, JSON.toJSON(request), 60);
        try {
            LuckyProductDrawerCondition condition = new LuckyProductDrawerCondition();
            BeanUtils.copyProperties(request, condition);
            condition.setBalance(balance);
            condition.setDiamondBalance(diamondBalance);
            condition.setPlayer(player);
            condition.setPay(pay);
            condition.setLuckyProduct(luckyProduct);
            condition.setRandomGiftProductDTO(randomGiftProductDTO);
            UserLuckyHistory userLuckyHistory = luckyProductDrawService.draw(condition);
            UserLuckyHistoryResponse response = new UserLuckyHistoryResponse();
            BeanUtils.copyProperties(userLuckyHistory, response);
            return response;
        } finally {
            redisTemplateFacde.delete(key);
        }
    }

    @Transactional
    @ConcurrencyLimit
    public List<ShopGiftProductResponse> lotteryDraw(int userId, Gift gift, LotteryDrawRequest request, String sign) {
        this.checkFishGiftExits(gift.getId());
        String lockKey = "LOCK:BOX:" + userId;
        RLock rLock = null;
        try {
            rLock = redissonLockService.acquire(lockKey, 6, TimeUnit.SECONDS);
            if (rLock == null) {
                throw BizClientException.of(CommonBizCode.COMMON_BUSY);
            }

            DrawBoxContext drawBoxContext = new DrawBoxContext(userId, gift);

            UserPlus player = userService.get(userId);
            BigDecimal pay = gift.getPrice().multiply(BigDecimal.valueOf(request.getNum()));
            BigDecimal balance = pay;
            BigDecimal diamondBalance = BigDecimal.ZERO;

            if (drawBoxContext.isKeyMode()) {
                keyBoxDrawService.useKey(userId, gift, request.getNum());
            } else {
                if (player.getBalance().compareTo(pay) < 0) {
                    throw BizClientException.of(CommonBizCode.USER_BALANCE_LACK);
                }
            }

            if (gift.getMembershipGrade() != null && null != userPrizeService.findByUserIdAndGiftId(userId, gift.getId())) {
                throw new ApiException(StandardExceptionCode.AUTH_FAILURE, "您已参与该VIP抽奖");
            }

            List<GiftProductRecordPlus> giftProductRecords = giftProductRecordService.findByGiftIds(Collections.singletonList(request.getId()), null);
            Map<Integer, GiftProductRecordPlus> giftProductRecordMap = giftProductRecords.stream().collect(Collectors.toMap(GiftProductRecordPlus::getGiftProductId, record -> record, (value1, value2) -> value1));
            Set<Integer> giftProductIds = giftProductRecords
                    .stream().map(GiftProductRecordPlus::getGiftProductId).collect(Collectors.toSet());
            SystemConfigFacade config = systemConfigService.findByPrefix(LUCKY_DRAW_PREFIX);
            List<LuckyGiftProduct> giftProductList = giftProductService.findByGiftProductIds(giftProductIds).stream()
                    .map(giftProduct -> {
                        LuckyGiftProduct product = new LuckyGiftProduct();
                        BeanUtils.copyProperties(giftProduct, product);
                        if (giftProductRecordMap.containsKey(giftProduct.getId())) {
                            String outProbabilityStr = giftProductRecordMap.get(giftProduct.getId()).getOutProbability();
                            int outProbability = org.springframework.util.StringUtils.isEmpty(outProbabilityStr) ? 0 : Integer.parseInt(outProbabilityStr);
                            product.setOutProbability(outProbability);
                            product.setProbability(outProbability(config, outProbability));
                            product.setWeight(giftProductRecordMap.get(giftProduct.getId()).getWeight());
                            product.setSpecialState(giftProductRecordMap.get(giftProduct.getId()).getSpecialState());
                        }
                        return product;
                    }).collect(Collectors.toList());
            //判断箱子物品最低价格是否大于开箱价格
            LuckyGiftProduct minLuckyGiftProduct = giftProductList.stream().min(Comparator.comparing(LuckyGiftProduct::getPrice)).orElse(null);
            BigDecimal minPrice = minLuckyGiftProduct.getPrice();
            if (minPrice.compareTo(gift.getPrice()) >= 0) {
                throw BizClientException.of(CommonBizCode.SYS_ERROR_HOME_OPEN);
            }

            LuckyGift luckyGift = new LuckyGift(gift, giftProductList, balance, diamondBalance);
            List<LuckyGiftProduct> drawList = luckyBoxDrawService.draw(gift, pay, luckyGift, player, config, request.getNum(), drawBoxContext);

            return drawList.stream().map(giftProduct -> {
                ShopGiftProductResponse response = new ShopGiftProductResponse();
                BeanUtils.copyProperties(giftProduct, response);
                return response;
            }).collect(Collectors.toList());
        } finally {
            redissonLockService.releaseLock(lockKey, rLock);
        }
    }

    @ConcurrencyLimit
    public int shopBuy(int userId, int giftProductId) {
        String lockKey = "SHOP:BUY:" + userId;
        RLock rLock = null;
        try {
            rLock = redissonLockService.acquire(lockKey, 3, TimeUnit.SECONDS);
            if (rLock == null) {
                throw BizClientException.of(CommonBizCode.COMMON_BUSY);
            }
            return shopService.buy(userId, giftProductId);
        } finally {
            redissonLockService.releaseLock(lockKey, rLock);
        }
    }

    @ConcurrencyLimit
    @Transactional(rollbackFor = Exception.class)
    public void sell(int userId, SellProductRequest request, String[] userMessageId, BigDecimal b) {
        log.info("[饰品出售] userId:{} userMessageId:{}", userId, request.getUserMessageId());

        String lockKey = "SELL:" + userId;
        RLock rLock = null;
        try {
            rLock = redissonLockService.acquire(lockKey, 3, TimeUnit.SECONDS);
            if (rLock == null) {
                throw BizClientException.of(CommonBizCode.COMMON_BUSY);
            }

            List<Integer> userMessageIdList = Arrays.stream(userMessageId).map(Integer::parseInt).collect(Collectors.toList());
            List<UserMessagePlus> userMessageList = userMessageService.findByIds(userMessageIdList, userId, GlobalConstants.USER_MESSAGE_INVENTORY);

            if (CollectionUtils.isEmpty(userMessageList)) {
                throw BizServerException.of(CommonBizCode.PRODUCT_SELL_FAIL);
            }

            userMessageList.forEach(item -> {
                if (ProductKindEnums.GIFT_KEY.getCode().equals(item.getProductKind()) || item.getProductName().contains(SystemConstant.GIFT_KET_PRODUCT_NAME)) {
                    log.error("钥匙饰品无法出售");
                    throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
                }
            });

            List<UserMessagePlus> sellMessage = new ArrayList<>();
            userMessageList.forEach(um -> {
                if (!GlobalConstants.USER_MESSAGE_INVENTORY.equals(um.getState())) {
                    throw BizServerException.of(CommonBizCode.PRODUCT_SELL_FAIL);
                }

                BigDecimal money = um.getMoney();
                BigDecimal sellMoney = money.multiply(b);

                UserMessageGiftPlus userMessageGift = userMessageGiftService.getByMessageId(um.getId());
                userMessageGift.setUserMessageId(um.getId());
                userMessageGift.setState(1);
                userMessageGift.setSellMoney(sellMoney);
                userMessageGiftService.updatePlus(userMessageGift);
                um.setState("1");
                userMessageService.updatePlus(um);

                UserPlus user = userService.get(userId);
                BigDecimal m = user.getDiamondBalance().add(sellMoney);
                user.setDiamondBalance(m);
                userService.update(user);
                addUserBalance(user, sellMoney);

                // 出售的信息
                sellMessage.add(um);
            });
            //背包流水详情记录
            userBackpackService.batchOutPackage(userId, "SELL", sellMessage);
        } finally {
            redissonLockService.releaseLock(lockKey, rLock);
        }
    }

    @ConcurrencyLimit
    public List<String> withdraw(int userId, WithdrawPropRequest request) {
        UserPlus userPlus = userService.get(userId);
        if (!org.springframework.util.StringUtils.hasText(userPlus.getSteam())) {
            throw BizClientException.of(CommonBizCode.STEAM_URL_NOT_EXISTS);
        }
        if (CollectionUtils.isEmpty(request.getMessageIds())) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        log.info("[提取] userId:{} messageIds:{}", userId, request.getMessageIds());

        String lockKey = "WITHDRAW:" + userPlus.getId();

        RLock rLock = null;
        try {
            rLock = redissonLockService.acquire(lockKey, 3, TimeUnit.SECONDS);
            if (rLock == null) {
                throw BizClientException.of(CommonBizCode.COMMON_BUSY);
            }
            return withdrawPropService.withdraw(request.getMessageIds(), userPlus.getId(), userPlus.getName());
        } finally {
            redissonLockService.releaseLock(lockKey, rLock);
        }
    }

    private BigDecimal outProbability(SystemConfigFacade config, int outProbability) {
        switch (outProbability) {
            case 1:
                return config.decimal(LUCKY_JACKPOT_LEVE1_RATE);
            case 2:
                return config.decimal(LUCKY_JACKPOT_LEVE2_RATE);
            case 3:
                return config.decimal(LUCKY_JACKPOT_LEVE3_RATE);
            case 4:
                return config.decimal(LUCKY_JACKPOT_LEVE4_RATE);
            case 5:
                return config.decimal(LUCKY_JACKPOT_LEVE5_RATE);
            default:
                return BigDecimal.ZERO;
        }
    }

    private void addUserBalance(UserPlus user, BigDecimal amount) {
        UserBalance userBalance = new UserBalance();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(BigDecimal.ZERO);
        userBalance.setDiamondAmount(amount);
        userBalance.setType(1); //收入
        userBalance.setRemark("道具出售");
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setCurrentDiamondAmount(user.getDiamondBalance());
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalanceService.add(userBalance);
    }

    @ConcurrencyLimit
    public ActivationCodeResult receiveCDK(int userId, ActivationCode code) {
        UserPlus userPlus = userService.get(userId);
        return activationCodeService.receive(userPlus, code);
    }

    /**
     * 获取指定返回随机价格
     *
     * @return
     */
    private RandomGiftProductDTO getRandomProduct() {
        BigDecimal minPrice = new BigDecimal(0.01);
        BigDecimal maxPrice = new BigDecimal(0.05);
        RandomGiftProductDTO randomGiftProductDTO = null;
        int searchCount = 0;
        //5次还未获取到饰品，则跳出查找
        while (randomGiftProductDTO == null) {
            if (searchCount >= 5) {
                break;
            }
            //随机价格
            BigDecimal randomPrice = this.makeRandom(minPrice.floatValue(), maxPrice.floatValue(), 2);
            randomGiftProductDTO = giftProductPlusMapper.getRandomGiftProductByPrice(randomPrice);
            searchCount++;
        }
        return randomGiftProductDTO;
    }


    /**
     * 生成指定范围，指定小数位数的随机数
     *
     * @param min   最小值
     * @param max   最大值
     * @param scale 小数位数
     * @return
     */
    BigDecimal makeRandom(float min, float max, int scale) {
        BigDecimal cha = new BigDecimal(Math.random() * (max - min) + min);
        return cha.setScale(scale, BigDecimal.ROUND_DOWN);//保留 scale 位小数，舍掉后面小数
    }

    /**
     * 判断礼包是否是钓鱼，如果是则不能开箱
     *
     * @param giftId
     * @return
     */
    private void checkFishGiftExits(Integer giftId) {
        LambdaQueryWrapper<FishConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FishConfig::getGiftId, giftId);
        List<FishConfig> fishConfigList = fishConfigMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(fishConfigList)) {
            throw new ApiException(StandardExceptionCode.AUTH_FAILURE, "该宝箱是归属钓鱼活动，不能参与首页开箱");
        }
    }
}
