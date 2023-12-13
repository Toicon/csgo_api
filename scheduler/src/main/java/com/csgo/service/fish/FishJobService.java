package com.csgo.service.fish;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csgo.domain.Gift;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.config.SystemConfigFacade;
import com.csgo.domain.plus.fish.FishBaitConfig;
import com.csgo.domain.plus.fish.FishUserActivity;
import com.csgo.domain.plus.fish.FishUserActivityHook;
import com.csgo.domain.plus.fish.FishUserPrize;
import com.csgo.domain.plus.gift.GiftProductRecordPlus;
import com.csgo.domain.plus.user.UserBalancePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.fish.FishBaitConfigMapper;
import com.csgo.mapper.plus.fish.FishUserActivityHookMapper;
import com.csgo.mapper.plus.fish.FishUserActivityMapper;
import com.csgo.mapper.plus.fish.FishUserPrizeMapper;
import com.csgo.mapper.plus.user.UserBalancePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.service.RedissonLockService;
import com.csgo.service.jackpot.FishAnchorJackpotService;
import com.csgo.service.jackpot.FishUserJackpotService;
import com.csgo.service.product.GiftProductRecordJobService;
import com.csgo.support.GlobalConstants;
import com.csgo.util.StringUtil;
import com.echo.framework.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.csgo.service.fish.LotteryDrawConstants.*;


@Slf4j
@Service
public class FishJobService {

    @Autowired
    private UserBalancePlusMapper userBalancePlusMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private FishUserPrizeMapper fishPrizeMapper;
    @Autowired
    private FishUserActivityMapper fishUserActivityMapper;
    @Autowired
    private FishUserActivityHookMapper fishUserActivityHookMapper;
    @Autowired
    private FishBaitConfigMapper fishBaitConfigMapper;
    @Autowired
    private GiftProductRecordJobService giftProductRecordService;
    @Autowired
    private FishDrawService fishDrawService;
    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private GiftService giftService;
    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private RedissonLockService redissonLockService;
    @Autowired
    private FishUserJackpotService fishUserJackpotService;
    @Autowired
    private FishAnchorJackpotService fishAnchorJackpotService;

    /**
     * 获取钓鱼时间到期明细
     *
     * @param sessionType
     * @return
     */
    public List<FishUserActivityHook> getActivityHookList(Integer sessionType) {
        LambdaQueryWrapper<FishUserActivityHook> activityHookWrapper = new LambdaQueryWrapper<>();
        activityHookWrapper.eq(FishUserActivityHook::getSessionType, sessionType);
        activityHookWrapper.eq(FishUserActivityHook::getHookState, YesOrNoEnum.NO.getCode());
        activityHookWrapper.le(FishUserActivityHook::getEndTime, new Date());
        return fishUserActivityHookMapper.selectList(activityHookWrapper);
    }

    /**
     * 执行钓鱼任务
     */
    @Transactional
    public void timeFish(FishUserActivityHook fishUserActivityHook) {
        if (fishUserActivityHook == null) {
            return;
        }
        FishUserActivity fishUserActivity = fishUserActivityMapper.selectById(fishUserActivityHook.getActivityId());
        if (fishUserActivity == null) {
            return;
        }
        int num = 1;
        if (fishUserActivity.getBaitId() != null) {
            FishBaitConfig fishBaitConfig = fishBaitConfigMapper.selectById(fishUserActivity.getBaitId());
            if (fishBaitConfig == null) {
                log.error("鱼饵配置信息不存在,定时执行失败");
                return;
            }
            num = fishBaitConfig.getOpenBox();
        }
        String lockKey = this.getFishActivityKey(fishUserActivity.getId() + "");
        RLock rLock = null;
        try {
            rLock = redissonLockService.acquire(lockKey, 10, TimeUnit.SECONDS);
            if (rLock == null) {
                log.error("活动id重复执行:{}", fishUserActivity.getId());
                return;
            }
            UserPlus player = userPlusMapper.selectById(fishUserActivity.getUserId());
            if (fishUserActivityHook.getTurn().equals(fishUserActivity.getTurnCount())) {
                //定时轮次结束
                fishUserActivity.setFailureTime(new Date());
                fishUserActivity.setFailureRemark("定时轮次结束");
                fishUserActivity.setFinishState(YesOrNoEnum.YES.getCode());
                fishUserActivity.setUpdateDate(new Date());
                fishUserActivityMapper.updateById(fishUserActivity);
            } else {
                //进入下一轮
                //获取货币消耗金额
                BigDecimal extractAmount = fishUserActivity.getPayPrice();
                //判断用户余额是否不足
                BigDecimal balance = BigDecimal.ZERO;
                if (player.getBalance() != null) {
                    balance = balance.add(player.getBalance());
                }
                if (balance.compareTo(extractAmount) < 0) {
                    //货币不足、轮次结束
                    fishUserActivity.setFailureTime(new Date());
                    fishUserActivity.setFailureRemark("余额不足");
                    fishUserActivity.setFinishState(YesOrNoEnum.YES.getCode());
                    fishUserActivity.setUpdateDate(new Date());
                    fishUserActivityMapper.updateById(fishUserActivity);
                } else {
                    String userKey = this.getFishUserKey(fishUserActivity.getUserId() + "");
                    RLock userLock = null;
                    try {
                        userLock = redissonLockService.acquire(userKey, 5, TimeUnit.SECONDS);
                        if (rLock == null) {
                            log.error("用户id重复执行:{}", fishUserActivity.getUserId());
                            return;
                        }
                        //扣除货币消耗金额
                        this.cost(player, extractAmount);
                        //获取下一场结束时间
                        Calendar endTime = Calendar.getInstance();
                        endTime.setTime(fishUserActivityHook.getEndTime());
                        endTime.add(Calendar.SECOND, fishUserActivityHook.getHookTime());
                        //新增下一场钓鱼明细
                        FishUserActivityHook fishUserActivityHookNext = new FishUserActivityHook();
                        fishUserActivityHookNext.setActivityId(fishUserActivity.getId());
                        fishUserActivityHookNext.setSessionType(fishUserActivity.getSessionType());
                        fishUserActivityHookNext.setUserId(fishUserActivity.getUserId());
                        fishUserActivityHookNext.setTurn(fishUserActivityHook.getTurn() + 1);
                        fishUserActivityHookNext.setHookTime(fishUserActivityHook.getHookTime());
                        fishUserActivityHookNext.setHookState(YesOrNoEnum.NO.getCode());
                        fishUserActivityHookNext.setBeginTime(fishUserActivityHook.getEndTime());
                        fishUserActivityHookNext.setEndTime(endTime.getTime());
                        fishUserActivityHookNext.setCreateBy(fishUserActivity.getCreateBy());
                        fishUserActivityHookNext.setCreateDate(new Date());
                        fishUserActivityHookMapper.insert(fishUserActivityHookNext);
                    } finally {
                        redissonLockService.releaseLock(userKey, userLock);
                    }
                }
            }
            //获取奖励
            LuckyGiftProduct luckyGiftProduct = this.fishDraw(player, fishUserActivity.getGiftId(), num);
            if (luckyGiftProduct == null) {
                log.error("钓鱼礼包开箱失败");
                return;
            }
            FishUserPrize fishUserPrize = new FishUserPrize();
            fishUserPrize.setGiftId(fishUserActivity.getGiftId());
            fishUserPrize.setActivityId(fishUserActivity.getId());
            fishUserPrize.setPayPrice(fishUserActivity.getPayPrice());
            fishUserPrize.setTurn(fishUserActivityHook.getTurn());
            fishUserPrize.setUserId(player.getId());
            fishUserPrize.setUserName(player.getUserName());
            fishUserPrize.setGiftProductId(luckyGiftProduct.getId());
            fishUserPrize.setGiftProductName(luckyGiftProduct.getName());
            fishUserPrize.setGiftProductPrice(luckyGiftProduct.getPrice());
            fishUserPrize.setGiftProductImage(luckyGiftProduct.getImg());
            fishUserPrize.setOutProbability(luckyGiftProduct.getOutProbability());
            fishUserPrize.setCreateDate(new Date());
            fishPrizeMapper.insert(fishUserPrize);
            //活动明细结束
            fishUserActivityHook.setHookState(YesOrNoEnum.YES.getCode());
            fishUserActivityHook.setFinishTime(new Date());
            fishUserActivityHook.setUpdateDate(new Date());
            fishUserActivityHookMapper.updateById(fishUserActivityHook);
        } finally {
            redissonLockService.releaseLock(lockKey, rLock);
        }
    }


    /**
     * 钓鱼礼包开箱
     *
     * @param player
     * @param giftId
     * @return
     */
    private LuckyGiftProduct fishDraw(UserPlus player, Integer giftId, Integer num) {
        Gift gift = giftService.queryGiftId(giftId);
        List<GiftProductRecordPlus> giftProductRecords = giftProductRecordService.findByGiftIds(Collections.singletonList(gift.getId()), null);
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
                    }
                    return product;
                }).collect(Collectors.toList());
        //判断箱子物品最低价格是否大于开箱价格
        LuckyGiftProduct minLuckyGiftProduct = giftProductList.stream().min(Comparator.comparing(LuckyGiftProduct::getPrice)).orElse(null);
        BigDecimal minPrice = minLuckyGiftProduct.getPrice();
        if (minPrice.compareTo(gift.getPrice()) >= 0) {
            log.error("钓鱼礼包最低物品大于礼包价格");
            return null;
        }
        LuckyGift luckyGift = new LuckyGift(gift, giftProductList, BigDecimal.ZERO, BigDecimal.ZERO);
        LuckyGiftProduct luckyGiftProduct = fishDrawService.draw(gift, luckyGift, player, config, num);
        return luckyGiftProduct;
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

    private UserBalancePlus cost(UserPlus user, BigDecimal price) {
        BigDecimal costBalance = price;
        BigDecimal costDiamondBalance = BigDecimal.ZERO;
        user.setBalance(user.getBalance().subtract(costBalance));
        userPlusMapper.updateById(user);
        UserBalancePlus userBalance = new UserBalancePlus();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(costBalance);
        userBalance.setDiamondAmount(costDiamondBalance);
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setCurrentDiamondAmount(user.getDiamondBalance());
        userBalance.setType(2); //支出
        userBalance.setRemark("钓鱼玩法");
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalancePlusMapper.insert(userBalance);
        if (GlobalConstants.RETAIL_USER_FLAG == user.getFlag()) {
            //散户入库金额
            BigDecimal stock = price.multiply(new BigDecimal(0.8)); //入库比例
            fishUserJackpotService.updateFishUserJackpot(stock, user);
        } else {
            //测试入库金额
            BigDecimal stock = price.multiply(new BigDecimal(0.9)); //入库比例
            fishAnchorJackpotService.updateFishAnchorJackpot(stock, user);
        }
        return userBalance;
    }

    /**
     * 钓鱼活动 redis key
     *
     * @return
     */
    private String getFishActivityKey(String activityId) {
        return Messages.format("LOCK:FISHACTIVITY:{}", activityId);
    }

    /**
     * 钓鱼用户 redis key
     *
     * @param userId
     * @return
     */
    private String getFishUserKey(String userId) {
        return Messages.format("LOCK:FISHUSER:{}", userId);
    }
}
