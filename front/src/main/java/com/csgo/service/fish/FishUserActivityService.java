package com.csgo.service.fish;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.fish.SearchFishMyUserPrizeCondition;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.Gift;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.config.SystemConfigFacade;
import com.csgo.domain.plus.fish.*;
import com.csgo.domain.plus.gift.GiftProductRecordPlus;
import com.csgo.domain.plus.user.UserBalancePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.exception.ServiceErrorException;
import com.csgo.framework.exception.BizClientException;
import com.csgo.mapper.plus.fish.*;
import com.csgo.mapper.plus.user.UserBalancePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.service.GiftProductService;
import com.csgo.service.config.SystemConfigService;
import com.csgo.service.gift.GiftProductRecordService;
import com.csgo.service.gift.GiftService;
import com.csgo.service.lock.RedissonLockService;
import com.csgo.service.lottery.FishDrawService;
import com.csgo.service.lottery.support.LuckyGift;
import com.csgo.service.lottery.support.LuckyGiftProduct;
import com.csgo.service.pay.PayService;
import com.csgo.service.user.UserService;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.DateUtilsEx;
import com.csgo.util.StringUtil;
import com.csgo.web.request.fish.FishActivityHookRequest;
import com.csgo.web.request.fish.FishGiveUpRequest;
import com.csgo.web.request.fish.FishTouchGiveUpRequest;
import com.csgo.web.response.fish.*;
import com.csgo.web.response.gift.ShopGiftProductResponse;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.csgo.service.lottery.support.LotteryDrawConstants.*;

/**
 * 钓鱼用户活动
 */
@Slf4j
@Service
public class FishUserActivityService {
    @Autowired
    private FishUserPrizeMapper fishPrizeMapper;
    @Autowired
    private FishUserActivityMapper fishUserActivityMapper;
    @Autowired
    private FishUserActivityHookMapper fishUserActivityHookMapper;
    @Autowired
    private FishConfigMapper fishConfigMapper;
    @Autowired
    private FishBaitConfigMapper fishBaitConfigMapper;
    @Autowired
    private GiftService giftService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private UserBalancePlusMapper userBalancePlusMapper;
    @Autowired
    private GiftProductRecordService giftProductRecordService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private PayService payService;
    @Autowired
    private FishDrawService fishDrawService;
    @Autowired
    private RedissonLockService redissonLockService;
    @Autowired
    private FishUserJackpotService fishUserJackpotService;
    @Autowired
    private FishAnchorJackpotService fishAnchorJackpotService;
    @Autowired
    private FishUserPrizeService fishUserPrizeService;
    //极速模式1.5分钟
    private static BigDecimal SPEED_YES = BigDecimal.valueOf(1.5);
    //非极速模式3分钟
    private static BigDecimal SPEED_NO = BigDecimal.valueOf(3);


    /**
     * 我的网箱
     *
     * @return
     */
    public Page<FishUserPrize> findMyUserPrize(SearchFishMyUserPrizeCondition condition) {
        return fishPrizeMapper.findMyUserPrize(condition);
    }

    /**
     * 最近挂机奖励结果
     *
     * @param userId
     * @param giftId
     * @return
     */
    @Transactional
    public List<FishUserPrizeResultResponse> findPrizeResult(Integer userId, Integer giftId) {
        List<FishUserPrizeResultResponse> responses = new ArrayList<>();
        List<FishUserPrize> fishUserPrizeList = fishPrizeMapper.findPrizeResult(userId, giftId);
        if (CollectionUtils.isNotEmpty(fishUserPrizeList)) {
            for (FishUserPrize prize : fishUserPrizeList) {
                prize.setPromptState(YesOrNoEnum.YES.getCode());
                FishUserPrizeResultResponse response = new FishUserPrizeResultResponse();
                response.setGiftProductName(prize.getGiftProductName());
                response.setGiftProductImage(prize.getGiftProductImage());
                response.setGiftProductPrice(prize.getGiftProductPrice());
                response.setOutProbability(prize.getOutProbability());
                responses.add(response);
            }
            //修改挂机状态
            fishUserPrizeService.updateBatchById(fishUserPrizeList);
        }
        return responses;
    }

    /**
     * 最近掉落
     *
     * @return
     */
    public List<FishUserPrizeFallResponse> findLastFall(Integer giftId) {
        List<FishUserPrizeFallResponse> responses = new ArrayList<>();
        List<FishUserPrize> fishUserPrizeList = fishPrizeMapper.findLastFall(giftId);
        if (CollectionUtils.isNotEmpty(fishUserPrizeList)) {
            for (FishUserPrize prize : fishUserPrizeList) {
                FishUserPrizeFallResponse response = new FishUserPrizeFallResponse();
                response.setGiftProductName(prize.getGiftProductName());
                response.setGiftProductImage(prize.getGiftProductImage());
                response.setGiftProductPrice(prize.getGiftProductPrice());
                response.setOutProbability(prize.getOutProbability());
                responses.add(response);
            }
        }
        return responses;
    }

    /**
     * 定时起竿
     *
     * @return
     */
    @Transactional
    public FishUserTimeGiveUpResponse timeGiveUp(Integer userId, Integer giftId, Integer turn) {
        //判断调用场景信息是否存在
        this.checkGiftExits(giftId);
        FishUserTimeGiveUpResponse response = new FishUserTimeGiveUpResponse();
        //获取活动信息
        FishUserActivity fishUserActivity = this.getActivityInfo(giftId, userId);
        FishUserPrize fishUserPrize;
        if (fishUserActivity != null) {
            //活动未结束
            fishUserPrize = fishPrizeMapper.getTimeGiveUp(giftId, userId, fishUserActivity.getId(), turn);
        } else {
            //活动已结束
            fishUserPrize = fishPrizeMapper.getTimeGiveUp(giftId, userId, turn);
        }
        if (fishUserPrize != null) {
            fishUserPrize.setPromptState(YesOrNoEnum.YES.getCode());
            fishPrizeMapper.updateById(fishUserPrize);
            response.setGiftProductName(fishUserPrize.getGiftProductName());
            response.setGiftProductImage(fishUserPrize.getGiftProductImage());
            response.setGiftProductPrice(fishUserPrize.getGiftProductPrice());
            response.setOutProbability(fishUserPrize.getOutProbability());
        } else {
            throw new ServiceErrorException("还没有猎物上钩，请等待~");
        }
        return response;
    }

    /**
     * 获取钓鱼活动配置信息
     *
     * @param sessionType
     * @param userId
     * @return
     */
    public List<FishConfigResponse> findConfigList(Integer sessionType, Integer userId) {
        List<FishConfigResponse> responses = new ArrayList<>();
        List<FishConfig> fishConfigList = fishConfigMapper.findBySessionType(sessionType);
        if (CollectionUtils.isNotEmpty(fishConfigList)) {
            for (FishConfig config : fishConfigList) {
                FishConfigResponse response = new FishConfigResponse();
                response.setGiftId(config.getGiftId());
                response.setSessionType(config.getSessionType());
                response.setFishRodType(config.getFishRodType());
                FishUserActivity fishUserActivity = this.getActivityInfo(config.getGiftId(), userId);
                if (fishUserActivity == null) {
                    response.setHookState(YesOrNoEnum.NO.getCode());
                } else {
                    response.setHookState(YesOrNoEnum.YES.getCode());
                }
                responses.add(response);
            }
        }
        return responses;
    }

    /**
     * 获取钓鱼活动鱼饵配置信息
     *
     * @return
     */
    public List<FishBaitConfigResponse> findBaitConfigList(Integer sessionType) {
        List<FishBaitConfigResponse> responses = new ArrayList<>();
        List<FishBaitConfig> fishBaitConfigList = fishBaitConfigMapper.findBySessionType(sessionType);
        if (CollectionUtils.isNotEmpty(fishBaitConfigList)) {
            for (FishBaitConfig config : fishBaitConfigList) {
                FishBaitConfigResponse response = new FishBaitConfigResponse();
                response.setBaitId(config.getId());
                response.setBaitName(config.getBaitName());
                response.setBaitImg(config.getBaitImg());
                response.setBaitBgImg(config.getBaitBgImg());
                response.setPayRatio(config.getPayRatio());
                response.setOpenBox(config.getOpenBox());
                responses.add(response);
            }
        }
        return responses;
    }

    /**
     * 一键起竿
     *
     * @param userId
     * @param name
     * @param request
     * @return
     */
    @Transactional
    public FishUserGiveUpResponse touchGiveUp(int userId, String name, FishTouchGiveUpRequest request) {
        FishUserGiveUpResponse response = new FishUserGiveUpResponse();
        //判断调用场景信息是否存在
        FishConfig fishConfig = this.checkGiftExits(request.getGiftId());
        //是否有充值提示
        payService.validateFishDraw(userId);
        Gift gift = giftService.queryGiftId(request.getGiftId());
        if (gift == null) {
            throw new ServiceErrorException("钓鱼奖励信息不存在");
        }
        String lockKey = this.getFishUserKey(userId + "");
        RLock rLock = null;
        try {
            rLock = redissonLockService.acquire(lockKey, 10, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new ServiceErrorException("不允许重复操作,请10秒后再试");
            }
            //获取活动信息
            FishUserActivity fishUserActivity = this.getActivityInfo(request.getGiftId(), userId);
            if (fishUserActivity != null) {
                throw new ServiceErrorException("钓鱼信息已存在，不能重复操作,请刷新页面");
            } else {
                BigDecimal payRatio = BigDecimal.ONE;
                int num = 1;
                if (request.getBaitId() != null) {
                    FishBaitConfig fishBaitConfig = fishBaitConfigMapper.selectById(request.getBaitId());
                    if (fishBaitConfig == null) {
                        throw new ServiceErrorException("鱼饵配置信息不存在，请联系客服人员");
                    }
                    payRatio = fishBaitConfig.getPayRatio();
                    num = fishBaitConfig.getOpenBox();
                }
                //获取货币消耗金额
                BigDecimal extractAmount = gift.getPrice().multiply(payRatio).setScale(2, BigDecimal.ROUND_DOWN);
                //判断用户余额是否不足
                UserPlus player = userService.get(userId);
                BigDecimal balance = BigDecimal.ZERO;
                if (player.getBalance() != null) {
                    balance = balance.add(player.getBalance());
                }
                if (balance.compareTo(extractAmount) < 0) {
                    throw BizClientException.of(CommonBizCode.USER_BALANCE_LACK);
                }
                //扣除货币消耗金额
                this.cost(player, extractAmount);
                //单轮挂机时间(秒)
                int turnHook = 0;
                //总轮数
                int turnCount = 1;
                //创建活动信息
                Date now = new Date();
                fishUserActivity = new FishUserActivity();
                fishUserActivity.setFishMode(YesOrNoEnum.NO.getCode());
                fishUserActivity.setSessionType(fishConfig.getSessionType());
                fishUserActivity.setGiftId(gift.getId());
                fishUserActivity.setBaitId(request.getBaitId());
                fishUserActivity.setPayPrice(extractAmount);
                fishUserActivity.setTurnCount(turnCount);
                fishUserActivity.setHookTime(turnHook);
                fishUserActivity.setBeginTime(now);
                fishUserActivity.setEndTime(now);
                fishUserActivity.setFinishState(YesOrNoEnum.YES.getCode());
                fishUserActivity.setUserId(userId);
                fishUserActivity.setCreateBy(name);
                fishUserActivity.setCreateDate(new Date());
                fishUserActivityMapper.insert(fishUserActivity);
                //创建活动明细
                FishUserActivityHook fishUserActivityHook = new FishUserActivityHook();
                fishUserActivityHook.setSessionType(fishConfig.getSessionType());
                fishUserActivityHook.setActivityId(fishUserActivity.getId());
                fishUserActivityHook.setUserId(userId);
                fishUserActivityHook.setTurn(turnCount);
                fishUserActivityHook.setHookTime(turnHook);
                fishUserActivityHook.setHookState(YesOrNoEnum.YES.getCode());
                fishUserActivityHook.setBeginTime(now);
                fishUserActivityHook.setEndTime(now);
                fishUserActivityHook.setFinishTime(now);
                fishUserActivityHook.setCreateBy(name);
                fishUserActivityHook.setCreateDate(new Date());
                fishUserActivityHookMapper.insert(fishUserActivityHook);
                //返回奖励信息
                ShopGiftProductResponse shopGiftProductResponse = this.fishDraw(player, gift.getId(), num);
                if (shopGiftProductResponse != null) {
                    FishUserPrize fishUserPrize = new FishUserPrize();
                    fishUserPrize.setGiftId(gift.getId());
                    fishUserPrize.setActivityId(fishUserActivity.getId());
                    fishUserPrize.setTurn(fishUserActivityHook.getTurn());
                    fishUserPrize.setPayPrice(fishUserActivity.getPayPrice());
                    fishUserPrize.setUserId(player.getId());
                    fishUserPrize.setUserName(player.getUserName());
                    fishUserPrize.setPromptState(YesOrNoEnum.YES.getCode());
                    fishUserPrize.setGiftProductId(shopGiftProductResponse.getId());
                    fishUserPrize.setGiftProductName(shopGiftProductResponse.getName());
                    fishUserPrize.setGiftProductPrice(shopGiftProductResponse.getPrice());
                    fishUserPrize.setGiftProductImage(shopGiftProductResponse.getImg());
                    fishUserPrize.setOutProbability(shopGiftProductResponse.getOutProbability());
                    fishUserPrize.setCreateDate(new Date());
                    fishPrizeMapper.insert(fishUserPrize);
                    //返回奖励信息
                    response.setOutProbability(shopGiftProductResponse.getOutProbability());
                    response.setGiftProductName(shopGiftProductResponse.getName());
                    response.setGiftProductImage(shopGiftProductResponse.getImg());
                    response.setGiftProductPrice(shopGiftProductResponse.getPrice());
                } else {
                    throw new ServiceErrorException("钓鱼奖励信息不存在，请联系客服人员");
                }
            }
        } finally {
            redissonLockService.releaseLock(lockKey, rLock);
        }
        return response;
    }


    /**
     * 活动挂机
     *
     * @param userId
     * @param name
     * @param request
     * @return
     */
    @Transactional
    public FishUserHookResponse hook(int userId, String name, FishActivityHookRequest request) {
        FishUserHookResponse response = new FishUserHookResponse();
        //判断挂机次数是否正确
        this.checkTurnCount(request.getTurnCount());
        //判断调用场景信息是否存在
        FishConfig fishConfig = this.checkGiftExits(request.getGiftId());
        //是否有充值提示
        payService.validateFishDraw(userId);
        Gift gift = giftService.queryGiftId(request.getGiftId());
        if (gift == null) {
            throw new ServiceErrorException("钓鱼奖励信息不存在");
        }
        String lockKey = this.getFishUserKey(userId + "");
        RLock rLock = null;
        try {
            rLock = redissonLockService.acquire(lockKey, 10, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new ServiceErrorException("不允许重复操作,请10秒后再试");
            }
            //获取活动信息
            FishUserActivity fishUserActivity = this.getActivityInfo(request.getGiftId(), userId);
            if (fishUserActivity != null) {
                throw new ServiceErrorException("钓鱼信息已存在，不能重复操作,请刷新页面");
            } else {
                BigDecimal payRatio = BigDecimal.ONE;
                if (request.getBaitId() != null) {
                    FishBaitConfig fishBaitConfig = fishBaitConfigMapper.selectById(request.getBaitId());
                    if (fishBaitConfig == null) {
                        throw new ServiceErrorException("鱼饵配置信息不存在，请联系客服人员");
                    }
                    payRatio = fishBaitConfig.getPayRatio();
                }
                //获取货币消耗金额
                BigDecimal extractAmount = gift.getPrice().multiply(payRatio).setScale(2, BigDecimal.ROUND_DOWN);
                //判断用户余额是否不足
                UserPlus player = userService.get(userId);
                BigDecimal balance = BigDecimal.ZERO;
                if (player.getBalance() != null) {
                    balance = balance.add(player.getBalance());
                }
                if (balance.compareTo(extractAmount) < 0) {
                    throw BizClientException.of(CommonBizCode.USER_BALANCE_LACK);
                }
                //扣除货币消耗金额
                this.cost(player, extractAmount);
                //单轮挂机时间(秒)
                int turnHook;
                //挂机时间(秒)
                int hookTime;
                if (request.getFishMode().equals(YesOrNoEnum.NO.getCode())) {
                    turnHook = this.SPEED_NO.multiply(BigDecimal.valueOf(60)).intValue();
                    hookTime = BigDecimal.valueOf(request.getTurnCount()).multiply(this.SPEED_NO).multiply(BigDecimal.valueOf(60)).intValue();
                } else {
                    turnHook = this.SPEED_YES.multiply(BigDecimal.valueOf(60)).intValue();
                    hookTime = BigDecimal.valueOf(request.getTurnCount()).multiply(this.SPEED_YES).multiply(BigDecimal.valueOf(60)).intValue();
                }
                //创建活动信息
                Date now = new Date();
                Calendar end = Calendar.getInstance();
                end.setTime(now);
                end.add(Calendar.SECOND, hookTime);
                fishUserActivity = new FishUserActivity();
                fishUserActivity.setFishMode(request.getFishMode());
                fishUserActivity.setSessionType(fishConfig.getSessionType());
                fishUserActivity.setGiftId(gift.getId());
                fishUserActivity.setBaitId(request.getBaitId());
                fishUserActivity.setPayPrice(extractAmount);
                fishUserActivity.setTurnCount(request.getTurnCount());
                fishUserActivity.setHookTime(hookTime);
                fishUserActivity.setBeginTime(now);
                fishUserActivity.setEndTime(end.getTime());
                fishUserActivity.setFinishState(YesOrNoEnum.NO.getCode());
                fishUserActivity.setUserId(userId);
                fishUserActivity.setCreateBy(name);
                fishUserActivity.setCreateDate(new Date());
                fishUserActivityMapper.insert(fishUserActivity);
                //创建活动明细
                Calendar turnHookTime = Calendar.getInstance();
                turnHookTime.setTime(now);
                turnHookTime.add(Calendar.SECOND, turnHook);
                FishUserActivityHook fishUserActivityHook = new FishUserActivityHook();
                fishUserActivityHook.setSessionType(fishConfig.getSessionType());
                fishUserActivityHook.setActivityId(fishUserActivity.getId());
                fishUserActivityHook.setUserId(userId);
                fishUserActivityHook.setTurn(1);
                fishUserActivityHook.setHookTime(turnHook);
                fishUserActivityHook.setHookState(YesOrNoEnum.NO.getCode());
                fishUserActivityHook.setBeginTime(now);
                fishUserActivityHook.setEndTime(turnHookTime.getTime());
                fishUserActivityHook.setCreateBy(name);
                fishUserActivityHook.setCreateDate(new Date());
                fishUserActivityHookMapper.insert(fishUserActivityHook);
                //返回倒计时
                response.setCountDown(DateUtilsEx.getDifferSecond(now, turnHookTime.getTime()).intValue());
                response.setTurn(fishUserActivityHook.getTurn());
                response.setRemainCount(fishUserActivity.getTurnCount() - fishUserActivityHook.getTurn());
            }
        } finally {
            redissonLockService.releaseLock(lockKey, rLock);
        }
        return response;
    }


    /**
     * 获取当前活动信息
     *
     * @param userId
     * @return
     */
    public FishUserInfoResponse getInfo(int userId, Integer giftId) {
        FishUserInfoResponse response = new FishUserInfoResponse();
        //判断调用场景信息是否存在
        this.checkGiftExits(giftId);
        Gift gift = giftService.queryGiftId(giftId);
        if (gift == null) {
            throw new ServiceErrorException("钓鱼奖励信息不存在");
        }
        response.setPayPrice(gift.getPrice());
        //获取活动信息
        FishUserActivity fishUserActivity = this.getActivityInfo(giftId, userId);
        if (fishUserActivity != null) {
            //获取活动明细
            Date now = new Date();
            FishUserActivityHook fishUserActivityHook = this.getActivityHookInfo(fishUserActivity.getId(), userId);
            response.setGiftId(fishUserActivity.getGiftId());
            response.setBaitId(fishUserActivity.getBaitId());
            response.setCountDown(DateUtilsEx.getDifferSecond(now, fishUserActivityHook.getEndTime()).intValue());
            response.setHookState(YesOrNoEnum.YES.getCode());
            response.setTurn(fishUserActivityHook.getTurn());
            response.setFishMode(fishUserActivity.getFishMode());
            response.setRemainCount(fishUserActivity.getTurnCount() - fishUserActivityHook.getTurn());
        } else {
            response.setHookState(YesOrNoEnum.NO.getCode());
            response.setFishMode(YesOrNoEnum.NO.getCode());
        }
        return response;
    }


    /**
     * 主动起竿
     *
     * @param userId
     * @param request
     * @return
     */
    @Transactional
    public FishUserGiveUpResponse giveUp(int userId, FishGiveUpRequest request) {
        FishUserGiveUpResponse response = new FishUserGiveUpResponse();
        Integer giftId = request.getGiftId();
        //判断调用场景信息是否存在
        this.checkGiftExits(giftId);
        //获取活动信息
        FishUserActivity fishUserActivity = this.getActivityInfo(giftId, userId);
        if (fishUserActivity == null) {
            throw new ServiceErrorException("钓鱼活动不存在，不能起竿操作,请刷新页面");
        }
        FishUserActivityHook fishUserActivityHook = this.getActivityHookInfo(fishUserActivity.getId(), userId);
        if (fishUserActivityHook == null) {
            throw new ServiceErrorException("钓鱼活动不存在，不能起竿操作,请刷新页面");
        }
        UserPlus player = userService.get(userId);
        String lockKey = this.getFishActivityKey(fishUserActivity.getId() + "");
        RLock rLock = null;
        try {
            rLock = redissonLockService.acquire(lockKey, 10, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new ServiceErrorException("不允许重复操作,请10秒后再试");
            }
            int num = 1;
            if (fishUserActivity.getBaitId() != null) {
                FishBaitConfig fishBaitConfig = fishBaitConfigMapper.selectById(fishUserActivity.getBaitId());
                if (fishBaitConfig == null) {
                    throw new ServiceErrorException("鱼饵配置信息不存在，请联系客服人员");
                }
                num = fishBaitConfig.getOpenBox();
            }
            ShopGiftProductResponse shopGiftProductResponse = this.fishDraw(player, giftId, num);
            if (shopGiftProductResponse != null) {
                FishUserPrize fishUserPrize = new FishUserPrize();
                fishUserPrize.setGiftId(giftId);
                fishUserPrize.setPromptState(YesOrNoEnum.YES.getCode());
                fishUserPrize.setActivityId(fishUserActivity.getId());
                fishUserPrize.setPayPrice(fishUserActivity.getPayPrice());
                fishUserPrize.setTurn(fishUserActivityHook.getTurn());
                fishUserPrize.setUserId(player.getId());
                fishUserPrize.setUserName(player.getUserName());
                fishUserPrize.setGiftProductId(shopGiftProductResponse.getId());
                fishUserPrize.setGiftProductName(shopGiftProductResponse.getName());
                fishUserPrize.setGiftProductPrice(shopGiftProductResponse.getPrice());
                fishUserPrize.setGiftProductImage(shopGiftProductResponse.getImg());
                fishUserPrize.setOutProbability(shopGiftProductResponse.getOutProbability());
                fishUserPrize.setCreateDate(new Date());
                fishPrizeMapper.insert(fishUserPrize);
                //返回奖励信息
                response.setOutProbability(shopGiftProductResponse.getOutProbability());
                response.setGiftProductName(shopGiftProductResponse.getName());
                response.setGiftProductImage(shopGiftProductResponse.getImg());
                response.setGiftProductPrice(shopGiftProductResponse.getPrice());
            } else {
                throw new ServiceErrorException("钓鱼活动不存在，不能起竿操作,请刷新页面");
            }
            //活动明细结束
            fishUserActivityHook.setHookState(YesOrNoEnum.YES.getCode());
            fishUserActivityHook.setFinishTime(new Date());
            fishUserActivityHook.setUpdateDate(new Date());
            fishUserActivityHookMapper.updateById(fishUserActivityHook);
            //活动结束
            fishUserActivity.setActiveState(YesOrNoEnum.YES.getCode());
            fishUserActivity.setFailureTime(new Date());
            fishUserActivity.setFailureRemark("主动起竿");
            fishUserActivity.setFinishState(YesOrNoEnum.YES.getCode());
            fishUserActivity.setUpdateDate(new Date());
            fishUserActivityMapper.updateById(fishUserActivity);
        } finally {
            redissonLockService.releaseLock(lockKey, rLock);
        }
        return response;
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
     * 获取活动信息
     *
     * @param giftId
     * @param userId
     * @return
     */
    private FishUserActivity getActivityInfo(Integer giftId, int userId) {
        LambdaQueryWrapper<FishUserActivity> activityWrapper = new LambdaQueryWrapper<>();
        activityWrapper.eq(FishUserActivity::getGiftId, giftId);
        activityWrapper.eq(FishUserActivity::getUserId, userId);
        activityWrapper.eq(FishUserActivity::getFinishState, YesOrNoEnum.NO.getCode());
        return fishUserActivityMapper.selectOne(activityWrapper);
    }

    /**
     * 获取当前活动明细
     *
     * @param activityId
     * @param userId
     * @return
     */
    private FishUserActivityHook getActivityHookInfo(Integer activityId, int userId) {
        LambdaQueryWrapper<FishUserActivityHook> activityHookWrapper = new LambdaQueryWrapper<>();
        activityHookWrapper.eq(FishUserActivityHook::getActivityId, activityId);
        activityHookWrapper.eq(FishUserActivityHook::getUserId, userId);
        activityHookWrapper.eq(FishUserActivityHook::getHookState, YesOrNoEnum.NO.getCode());
        return fishUserActivityHookMapper.selectOne(activityHookWrapper);
    }

    /**
     * 判断礼包是否存在
     *
     * @param giftId
     * @return
     */
    private FishConfig checkGiftExits(Integer giftId) {
        LambdaQueryWrapper<FishConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FishConfig::getGiftId, giftId);
        List<FishConfig> fishConfigList = fishConfigMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(fishConfigList)) {
            throw new ServiceErrorException("钓鱼场景信息不存在");
        } else {
            return fishConfigList.get(0);
        }
    }

    /**
     * 判断挂机次数是否正确
     *
     * @param turnCount
     * @return
     */
    private void checkTurnCount(Integer turnCount) {
        List<Integer> turnCountList = new ArrayList<>();
        turnCountList.add(10);
        turnCountList.add(20);
        turnCountList.add(50);
        turnCountList.add(100);
        turnCountList.add(200);
        turnCountList.add(600);
        if (!turnCountList.contains(turnCount)) {
            throw new ServiceErrorException("挂机次数不正确");
        }
    }

    /**
     * 钓鱼开箱
     *
     * @param player
     * @param giftId
     * @param num
     * @return
     */
    private ShopGiftProductResponse fishDraw(UserPlus player, Integer giftId, Integer num) {
        Gift gift = giftService.queryGiftId(giftId);
        if (gift == null) {
            throw new ServiceErrorException("钓鱼奖励信息不存在");
        }
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
            throw BizClientException.of(CommonBizCode.SYS_ERROR_HOME_OPEN);
        }
        LuckyGift luckyGift = new LuckyGift(gift, giftProductList, BigDecimal.ZERO, BigDecimal.ZERO);
        LuckyGiftProduct giftProduct = fishDrawService.draw(gift, luckyGift, player, config, num);
        ShopGiftProductResponse response = new ShopGiftProductResponse();
        BeanUtils.copyProperties(giftProduct, response);
        return response;
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

    /**
     * 调用用户 redis key
     *
     * @return
     */
    private String getFishUserKey(String userId) {
        return Messages.format("LOCK:FISH:{}", userId);
    }

    /**
     * 钓鱼活动 redis key
     *
     * @return
     */
    private String getFishActivityKey(String activityId) {
        return Messages.format("LOCK:FISHACTIVITY:{}", activityId);
    }
}
