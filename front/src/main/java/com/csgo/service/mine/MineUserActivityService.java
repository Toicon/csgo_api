package com.csgo.service.mine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.enums.PassStateEnum;
import com.csgo.domain.enums.PrizeStateEnum;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.gift.MineGiftProductDTO;
import com.csgo.domain.plus.mine.*;
import com.csgo.domain.plus.user.UserBalancePlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.user.UserPrizePlus;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.exception.ServiceErrorException;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.exception.BizServerException;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.mine.MineUserActivityMapper;
import com.csgo.mapper.plus.mine.MineUserActivityPassLevelMapper;
import com.csgo.mapper.plus.mine.MineUserConfigPriceMapper;
import com.csgo.mapper.plus.mine.MineUserPrizeMapper;
import com.csgo.mapper.plus.user.UserBalancePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.backpack.BackpackFromSourceConstant;
import com.csgo.modular.backpack.logic.UserMessageLogic;
import com.csgo.service.UserMessageItemRecordService;
import com.csgo.service.UserMessageRecordService;
import com.csgo.service.UserMessageService;
import com.csgo.service.gift.UserMessageGiftService;
import com.csgo.service.user.UserPrizeService;
import com.csgo.service.user.UserService;
import com.csgo.support.ConcurrencyLimit;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.StringUtil;
import com.csgo.web.request.mine.MineUserActivityLastDropDetailsRequest;
import com.csgo.web.request.mine.MineUserActivityPassLevelRequest;
import com.csgo.web.request.mine.MineUserConfigPriceRequest;
import com.csgo.web.response.mine.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 扫雷用户活动
 *
 * @author admin
 */
@Slf4j
@Service
public class MineUserActivityService {
    @Autowired
    private MineUserPrizeMapper minePrizeMapper;
    @Autowired
    private MineUserActivityMapper mineUserActivityMapper;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;
    @Autowired
    private MineUserActivityPassLevelMapper mineUserActivityPassLevelMapper;
    @Autowired
    private MineUserActivityPrizeService mineUserActivityPrizeService;
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

    @Autowired
    private MineJackpotService mineJackpotService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPlusMapper userPlusMapper;

    @Autowired
    private UserBalancePlusMapper userBalancePlusMapper;

    @Autowired
    private UserPrizeService userPrizeService;

    @Autowired
    private MineUserConfigPriceMapper mineUserConfigPriceMapper;

    //支付货币公式系数
    private static BigDecimal PAY_RATIO = BigDecimal.valueOf(2.7);

    //第一层最低价格
    private static BigDecimal ONE_LEVEL_PRICE_MIN = BigDecimal.valueOf(1.2);
    //第一层最高价格
    private static BigDecimal ONE_LEVEL_PRICE_MAX = BigDecimal.valueOf(40);

    //第二层最低系数
    private static BigDecimal TWO_LEVEL_RATIO_MIN = BigDecimal.valueOf(1.1);
    //第二层最高系数
    private static BigDecimal TWO_LEVEL_RATIO_MAX = BigDecimal.valueOf(1.5);

    //第三层最低系数
    private static BigDecimal THREE_LEVEL_RATIO_MIN = BigDecimal.valueOf(1.9);
    //第三层最高系数
    private static BigDecimal THREE_LEVEL_RATIO_MAX = BigDecimal.valueOf(2.75);

    //第四层最低系数
    private static BigDecimal FOUR_LEVEL_RATIO_MIN = BigDecimal.valueOf(1.75);
    //第四层最高系数
    private static BigDecimal FOUR_LEVEL_RATIO_MAX = BigDecimal.valueOf(2.5);

    //第五层最低系数
    private static BigDecimal FIVE_LEVEL_RATIO_MIN = BigDecimal.valueOf(2.15);
    //第五层最高系数
    private static BigDecimal FIVE_LEVEL_RATIO_MAX = BigDecimal.valueOf(8.5);

    //第六层最低系数
    private static BigDecimal SIX_LEVEL_RATIO_MIN = BigDecimal.valueOf(1.9);
    //第六层最高系数
    private static BigDecimal SIX_LEVEL_RATIO_MAX = BigDecimal.valueOf(2.75);
    //总关数
    private static Integer LEVEL_SUM = 6;

    //测试账号少雷关数
    private static Integer TEST_LEVEL_SUM = 3;
    //测试账号少雷个数
    private static Integer TEST_MINE_SUM = 1;

    /**
     * 奖励价格区间设置
     *
     * @param userId
     * @param name
     */
    @Transactional
    public void priceConfig(int userId, String name, MineUserConfigPriceRequest request) {
        if (request.getMinPrice() == null) {
            throw new ServiceErrorException("起始金额不能为空");
        }
        if (request.getMaxPrice() == null) {
            throw new ServiceErrorException("截止金额不能为空");
        }
        if (request.getMinPrice().compareTo(request.getMaxPrice()) > 0) {
            throw new ServiceErrorException("起始金额不能大于截止金额");
        }
        if (request.getMinPrice().compareTo(BigDecimal.ZERO) == 0 && request.getMaxPrice().compareTo(BigDecimal.ZERO) == 0) {
            this.deleteMineUserConfigPrice(userId);
        } else {
            List<BigDecimal> minPriceMap = new ArrayList<>();
            minPriceMap.add(BigDecimal.valueOf(1L));
            minPriceMap.add(BigDecimal.valueOf(10L));
            minPriceMap.add(BigDecimal.valueOf(20L));
            minPriceMap.add(BigDecimal.valueOf(30L));
            minPriceMap.add(BigDecimal.valueOf(40L));
            List<BigDecimal> maxPriceMap = new ArrayList<>();
            maxPriceMap.add(BigDecimal.valueOf(10L));
            maxPriceMap.add(BigDecimal.valueOf(20L));
            maxPriceMap.add(BigDecimal.valueOf(30L));
            maxPriceMap.add(BigDecimal.valueOf(40L));
            maxPriceMap.add(BigDecimal.valueOf(50L));
            if (!minPriceMap.contains(request.getMinPrice())) {
                throw new ServiceErrorException("起始金额不在范围内");
            }
            if (!maxPriceMap.contains(request.getMaxPrice())) {
                throw new ServiceErrorException("截止金额不在范围内");
            }
            MineUserConfigPrice mineUserConfigPrice = this.getMineUserConfigPrice(userId);
            if (mineUserConfigPrice == null) {
                mineUserConfigPrice = new MineUserConfigPrice();
                mineUserConfigPrice.setUserId(userId);
                mineUserConfigPrice.setMinPrice(request.getMinPrice());
                mineUserConfigPrice.setMaxPrice(request.getMaxPrice());
                mineUserConfigPrice.setCreateDate(new Date());
                mineUserConfigPrice.setCreateBy(name);
                mineUserConfigPriceMapper.insert(mineUserConfigPrice);
            } else {
                mineUserConfigPrice.setMinPrice(request.getMinPrice());
                mineUserConfigPrice.setMaxPrice(request.getMaxPrice());
                mineUserConfigPrice.setUpdateDate(new Date());
                mineUserConfigPrice.setUpdateBy(name);
                mineUserConfigPriceMapper.updateById(mineUserConfigPrice);
            }
        }
    }

    /**
     * 活动奖励抽取
     *
     * @param userId
     * @param name
     * @return
     */
    @ConcurrencyLimit
    @Transactional
    public MineUserActivityResponse extractReward(int userId, String name) {
        MineUserActivityResponse response = new MineUserActivityResponse();
        //获取活动信息
        MineUserActivity mineUserActivity = this.getActivityInfo(userId, name);
        if (mineUserActivity.getPayState().equals(YesOrNoEnum.YES.getCode())) {
            throw new ServiceErrorException("活动已经支付，不能重置,请刷新页面");
        }
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        //获取用户奖励价格区间
        MineUserConfigPrice mineUserConfigPrice = this.getMineUserConfigPrice(userId);
        if (mineUserConfigPrice != null) {
            minPrice = mineUserConfigPrice.getMinPrice();
            maxPrice = mineUserConfigPrice.getMaxPrice();
        }
        //获取闯关信息
        List<MineUserActivityPassLevelItem> passLevelItems = this.getPassLevelItems(userId, name, mineUserActivity.getId());
        //获取每关奖品信息
        List<MineUserActivityPrizeItem> prizeItems = this.getUserConfigPrizeItems(userId, name, mineUserActivity.getId(), minPrice, maxPrice);
        //获取货币消耗金额
        BigDecimal extractAmount = this.getExtractAmount(prizeItems);
        response.setExtractAmount(extractAmount);
        response.setPrizeItems(prizeItems);
        response.setPassLevelItems(passLevelItems);
        response.setPayState(YesOrNoEnum.NO.getCode());
        response.setMinPrice(minPrice);
        response.setMaxPrice(maxPrice);
        return response;
    }

    /**
     * 活动支付
     *
     * @param userId
     * @return
     */
    @ConcurrencyLimit
    @Transactional
    public void pay(int userId) {
        //获取活动信息
        MineUserActivity mineUserActivity = this.getActivityInfo(userId);
        if (mineUserActivity != null) {
            if (mineUserActivity.getPayState().equals(YesOrNoEnum.YES.getCode())) {
                throw new ServiceErrorException("活动已经支付，不能重复支付,请刷新页面");
            }
            //获取每关奖品信息
            List<MineUserActivityPrizeItem> prizeItems = this.getPrizeItems(userId, mineUserActivity.getId());
            //获取货币消耗金额
            BigDecimal extractAmount = this.getExtractAmount(prizeItems);
            //判断用户余额是否不足
            UserPlus player = userService.get(userId);
            BigDecimal balance = BigDecimal.ZERO;
            if (player.getDiamondBalance() != null) {
                balance = balance.add(player.getBalance());
            }
            if (balance.compareTo(extractAmount) < 0) {
                throw new ServiceErrorException("账户V币不足，请充值后再试");
            }
            //扣除货币消耗金额
            this.cost(player, extractAmount);
            //入库金额
            BigDecimal stock = extractAmount.multiply(new BigDecimal(0.65)); //入库比例
            mineJackpotService.updateMineJackpot(stock, player);
            mineUserActivity.setPayState(YesOrNoEnum.YES.getCode());
            mineUserActivity.setPayPrice(extractAmount);
            mineUserActivity.setUpdateDate(new Date());
            mineUserActivityMapper.updateById(mineUserActivity);
        } else {
            throw new ServiceErrorException("活动信息不存在，不能进行支付,请刷新页面");
        }
    }

    /**
     * 获取当前活动信息
     *
     * @param userId
     * @return
     */
    public MineUserActivityResponse getInfo(int userId) {
        MineUserActivityResponse response = new MineUserActivityResponse();
        //获取活动信息
        MineUserActivity mineUserActivity = this.getActivityInfo(userId);
        if (mineUserActivity != null) {
            //获取闯关信息
            List<MineUserActivityPassLevelItem> passLevelItems = this.getPassLevelItems(userId, mineUserActivity.getId());
            //获取每关奖品信息
            List<MineUserActivityPrizeItem> prizeItems = this.getPrizeItems(userId, mineUserActivity.getId());
            //获取货币消耗金额
            BigDecimal extractAmount = this.getExtractAmount(prizeItems);
            response.setExtractAmount(extractAmount);
            response.setPrizeItems(prizeItems);
            response.setPassLevelItems(passLevelItems);
            response.setPayState(mineUserActivity.getPayState());
        }
        //获取用户奖励价格区间
        MineUserConfigPrice mineUserConfigPrice = this.getMineUserConfigPrice(userId);
        if (mineUserConfigPrice != null) {
            response.setMinPrice(mineUserConfigPrice.getMinPrice());
            response.setMaxPrice(mineUserConfigPrice.getMaxPrice());
        }
        return response;
    }

    /**
     * 获取放弃挑战奖励信息
     *
     * @param userId
     * @return
     */
    public MineUserGiveUpPrizeResponse getGiveUpPrize(int userId) {
        //获取活动信息
        MineUserActivity mineUserActivity = this.getActivityInfo(userId);
        if (mineUserActivity == null) {
            throw new ServiceErrorException("活动信息不存在，不能放弃挑战,请刷新页面");
        }
        if (mineUserActivity.getPayState().equals(YesOrNoEnum.NO.getCode())) {
            throw new ServiceErrorException("活动未支付，不能放弃挑战,请刷新页面");
        }
        MineUserGiveUpPrizeResponse response = new MineUserGiveUpPrizeResponse();
        //获取闯关信息
        List<MineUserActivityPassLevelItem> passLevelItems = this.getPassLevelItems(userId, mineUserActivity.getId());
        if (passLevelItems.size() == 1) {
            //第一关放弃获取保底奖励
            response.setLevel(0);
        } else {
            MineUserActivityPassLevelItem mineUserActivityPassLevelItem = passLevelItems.get(passLevelItems.size() - 1);
            if (!mineUserActivityPassLevelItem.getPassState().equals(PassStateEnum.INIT.getCode())) {
                throw new ServiceErrorException("活动已结束，不能放弃挑战,请刷新页面");
            }
            MineUserActivityPassLevelItem userActivityPassLevelItem = passLevelItems.get(passLevelItems.size() - 2);
            //获取每关奖品信息
            List<MineUserActivityPrizeItem> prizeItems = this.getPrizeItems(userId, mineUserActivity.getId());
            //获取放弃挑战奖励
            MineUserActivityPrizeItem prizeItem = prizeItems.stream().filter(item -> item.getLevel().equals(userActivityPassLevelItem.getLevel())).findFirst().orElse(null);
            Integer giftProductId = prizeItem.getGiftProductId();
            GiftProductPlus giftProduct = giftProductPlusMapper.selectById(giftProductId);
            if (giftProduct == null) {
                throw new ServiceErrorException("奖励信息不存在，请联系管理员");
            }
            //返回奖品详细信息
            response.setGiftProductName(giftProduct.getName());
            response.setGiftProductImg(giftProduct.getImg());
            response.setGiftProductPrice(giftProduct.getPrice());
            response.setExteriorName(giftProduct.getExteriorName());
            response.setLevel(prizeItem.getLevel());
        }
        return response;
    }

    /**
     * 放弃挑战
     *
     * @param userId
     * @param name
     * @return
     */
    @ConcurrencyLimit
    @Transactional
    public MineUserResultResponse giveUp(int userId, String name) {
        MineUserResultResponse response = new MineUserResultResponse();
        //获取活动信息
        MineUserActivity mineUserActivity = this.getActivityInfo(userId);
        if (mineUserActivity == null) {
            throw new ServiceErrorException("活动信息不存在，不能放弃挑战,请刷新页面");
        }
        if (mineUserActivity.getPayState().equals(YesOrNoEnum.NO.getCode())) {
            throw new ServiceErrorException("活动未支付，不能放弃挑战,请刷新页面");
        }
        //获取闯关信息
        Integer giftProductId;
        List<MineUserActivityPassLevelItem> passLevelItems = this.getPassLevelItems(userId, mineUserActivity.getId());
        if (passLevelItems.size() == 1) {
            //第一关放弃获取保底奖励
            MineGiftProductDTO mineGiftProductDTO = this.getMineRandomProduct();
            if (mineGiftProductDTO == null) {
                throw BizClientException.of(CommonBizCode.SYS_ERROR_MINE_OPEN);
            }
            giftProductId = mineGiftProductDTO.getGiftProductId();
        } else {
            MineUserActivityPassLevelItem mineUserActivityPassLevelItem = passLevelItems.get(passLevelItems.size() - 1);
            if (!mineUserActivityPassLevelItem.getPassState().equals(PassStateEnum.INIT.getCode())) {
                throw new ServiceErrorException("活动已结束，不能放弃挑战,请刷新页面");
            }
            MineUserActivityPassLevelItem userActivityPassLevelItem = passLevelItems.get(passLevelItems.size() - 2);
            //获取每关奖品信息
            List<MineUserActivityPrizeItem> prizeItems = this.getPrizeItems(userId, mineUserActivity.getId());
            //获取放弃挑战奖励
            MineUserActivityPrizeItem prizeItem = prizeItems.stream().filter(item -> item.getLevel().equals(userActivityPassLevelItem.getLevel())).findFirst().orElse(null);
            giftProductId = prizeItem.getGiftProductId();
        }
        GiftProductPlus giftProduct = giftProductPlusMapper.selectById(giftProductId);
        if (giftProduct == null) {
            throw new ServiceErrorException("奖励信息不存在，请联系管理员");
        }
        //奖品入背包
        UserPlus player = userService.get(userId);
        this.record(player, giftProduct);
        //记录用户奖励信息
        this.saveMineUserPrize(mineUserActivity.getUserId(), mineUserActivity.getId(),
                player.getUserName(), giftProduct.getId(), giftProduct.getName(), giftProduct.getPrice(), mineUserActivity.getPayPrice());
        //设置活动状态为已结束
        mineUserActivity.setFinishState(YesOrNoEnum.YES.getCode());
        mineUserActivity.setUpdateDate(new Date());
        mineUserActivity.setUpdateBy(name);
        mineUserActivityMapper.updateById(mineUserActivity);
        //返回奖品详细信息
        response.setGiftProductName(giftProduct.getName());
        response.setGiftProductImg(giftProduct.getImg());
        response.setGiftProductPrice(giftProduct.getPrice());
        response.setExteriorName(giftProduct.getExteriorName());
        return response;
    }

    /**
     * 最近掉落列表
     *
     * @return
     */
    public List<MineUserLastDropResponse> findLastDropList() {
        List<MineUserActivityPassLevel> lastDropPassList = mineUserActivityPassLevelMapper.findLastDropList();
        if (CollectionUtils.isEmpty(lastDropPassList)) {
            return null;
        }
        Set activityIds = lastDropPassList.stream().map(MineUserActivityPassLevel::getActivityId).collect(Collectors.toSet());
        List<MineUserPrize> lastDropList = minePrizeMapper.findLastDropList(activityIds);
        if (CollectionUtils.isEmpty(lastDropList)) {
            return null;
        } else {
            List<MineUserLastDropResponse> responses = new ArrayList<>();
            Map<Integer, UserPlus> userMap = new HashMap<>();
            Map<Integer, GiftProductPlus> productMap = new HashMap<>();
            Set userIds = lastDropList.stream().map(MineUserPrize::getUserId).collect(Collectors.toSet());
            Set productIds = lastDropList.stream().map(MineUserPrize::getGiftProductId).collect(Collectors.toSet());
            if (userIds != null && userIds.size() > 0) {
                userMap.putAll(userService.findByIds(new ArrayList<Integer>(userIds)).stream().collect(Collectors.toMap(UserPlus::getId, user -> user)));
            }
            if (productIds != null && productIds.size() > 0) {
                productMap.putAll(giftProductPlusMapper.findByIds(new ArrayList<Integer>(productIds)).stream().collect(Collectors.toMap(GiftProductPlus::getId, giftProductPlus -> giftProductPlus)));
            }
            for (MineUserPrize mineUserPrize : lastDropList) {
                MineUserLastDropResponse response = new MineUserLastDropResponse();
                response.setId(mineUserPrize.getId());
                response.setGiftProductPrice(mineUserPrize.getPrizePrice());
                if (productMap != null && productMap.containsKey(mineUserPrize.getGiftProductId())) {
                    GiftProductPlus giftProductPlus = productMap.get(mineUserPrize.getGiftProductId());
                    response.setGiftProductName(giftProductPlus.getName());
                    response.setGiftProductImg(giftProductPlus.getImg());
                }
                if (userMap != null && userMap.containsKey(mineUserPrize.getUserId())) {
                    UserPlus user = userMap.get(mineUserPrize.getUserId());
                    response.setNickName(user.getName());
                    response.setUserImg(user.getImg());
                }
                responses.add(response);
            }
            return responses;
        }
    }

    /**
     * 最近掉落详情
     *
     * @return
     */
    public List<MineUserLastDropDetailsResponse> findLastDropDetails(MineUserActivityLastDropDetailsRequest request) {
        MineUserPrize mineUserPrize = minePrizeMapper.selectById(request.getId());
        if (mineUserPrize == null) {
            throw BizClientException.of(CommonBizCode.COMMON_DATA_NOT_FOUND);
        } else {
            //最近掉落详情
            List<MineUserLastDropDetailsResponse> responses = new ArrayList<>();
            LambdaQueryWrapper<MineUserActivityPassLevel> activityPassLevelWrapper = new LambdaQueryWrapper<>();
            activityPassLevelWrapper.eq(MineUserActivityPassLevel::getUserId, mineUserPrize.getUserId());
            activityPassLevelWrapper.eq(MineUserActivityPassLevel::getActivityId, mineUserPrize.getActivityId());
            activityPassLevelWrapper.isNotNull(MineUserActivityPassLevel::getSelectLattice);
            activityPassLevelWrapper.orderByAsc(MineUserActivityPassLevel::getLevel);
            List<MineUserActivityPassLevel> activityPassLevelList = mineUserActivityPassLevelMapper.selectList(activityPassLevelWrapper);
            if (CollectionUtils.isEmpty(activityPassLevelList)) {
                //第一关放弃,返回空信息
                return null;
            } else {
                //获取每关奖品信息
                List<MineUserActivityPrizeItem> prizeItems = this.getPrizeItems(mineUserPrize.getUserId(), mineUserPrize.getActivityId());
                Map<Integer, MineUserActivityPrizeItem> prizeMap = prizeItems.stream().collect(Collectors.toMap(MineUserActivityPrizeItem::getLevel, mineUserActivityPrizeItem -> mineUserActivityPrizeItem));
                //遍历每关奖品信息
                for (MineUserActivityPassLevel passLevel : activityPassLevelList) {
                    MineUserLastDropDetailsResponse response = new MineUserLastDropDetailsResponse();
                    response.setLevel(passLevel.getLevel());
                    response.setMineLattice(Arrays.stream(passLevel.getMineLattice().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
                    response.setNonMineLattice(Arrays.stream(passLevel.getNonMineLattice().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
                    response.setSelectLattice(passLevel.getSelectLattice());
                    if (prizeMap != null && prizeMap.containsKey(passLevel.getLevel())) {
                        MineUserActivityPrizeItem item = prizeMap.get(passLevel.getLevel());
                        response.setGiftProductName(item.getGiftProductName());
                        response.setGiftProductImg(item.getGiftProductImg());
                        response.setGiftProductPrice(item.getGiftProductPrice());
                    }
                    responses.add(response);
                }
                return responses;
            }
        }
    }

    /**
     * 活动闯关
     *
     * @param userId
     * @param name
     * @param request
     * @param isTest
     * @return
     */
    @ConcurrencyLimit
    @Transactional
    public MineUserResultResponse passLevel(int userId, String name, MineUserActivityPassLevelRequest request, boolean isTest) {
        MineUserResultResponse response = new MineUserResultResponse();
        if (request.getId() == null) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        if (request.getSelectLattice() == null) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        //获取活动信息
        MineUserActivity mineUserActivity = this.getActivityInfo(userId);
        if (mineUserActivity == null) {
            throw new ServiceErrorException("活动信息不存在，不能进行闯关,请刷新页面");
        }
        if (mineUserActivity.getPayState().equals(YesOrNoEnum.NO.getCode())) {
            throw new ServiceErrorException("活动未支付，不能进行闯关,请刷新页面");
        }
        //获取闯关信息
        List<MineUserActivityPassLevel> passLevelList = this.getMineUserActivityPassLevelList(userId, mineUserActivity.getId());
        //当前闯关记录
        MineUserActivityPassLevel currentPassLevel = passLevelList.get(passLevelList.size() - 1);
        if (currentPassLevel.getId() != request.getId().intValue()) {
            throw new ServiceErrorException("闯关信息不存在,请刷新页面");
        }
        if (!currentPassLevel.getPassState().equals(PassStateEnum.INIT.getCode())) {
            throw new ServiceErrorException("活动已结束，不能进行闯关,请刷新页面");
        }
        List<MineUserActivityPrizeItem> prizeItems = this.getPrizeItems(userId, mineUserActivity.getId());
        //判断闯关答案是否正确
        boolean isAnswerCorrect = false;
        if (currentPassLevel.getNonMineLattice().contains(request.getSelectLattice().toString())) {
            isAnswerCorrect = true;
        }
        //获取本关奖品信息
        MineUserActivityPrizeItem prizeItem = prizeItems.stream().filter(item -> item.getLevel().equals(currentPassLevel.getLevel())).findFirst().orElse(null);
        GiftProductPlus giftProduct = giftProductPlusMapper.selectById(prizeItem.getGiftProductId());
        if (giftProduct == null) {
            log.error("奖励信息不存在，请联系管理员");
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        if (isAnswerCorrect) {
            //散户：闯关答案正确需要判断当前库存是否足够支出本关奖品
            if (!isTest) {
                //1、获取当前库存
                BigDecimal balance = mineJackpotService.getMineJackpotBalance();
                //2、判断当前库存是否足够支出本关奖品
                if (balance.compareTo(giftProduct.getPrice()) < 0) {
                    isAnswerCorrect = false;
                    //替换答案为不正确
                    List<Integer> mineLatticeList = Arrays.stream(currentPassLevel.getMineLattice().split(",")).map(Integer::parseInt).collect(Collectors.toList());
                    String mineLattice = mineLatticeList.get(0).toString();
                    String selectLattice = request.getSelectLattice().toString();
                    currentPassLevel.setNonMineLattice(currentPassLevel.getNonMineLattice().replace(selectLattice, mineLattice));
                    currentPassLevel.setMineLattice(currentPassLevel.getMineLattice().replace(mineLattice, selectLattice));
                }
            }
        } else {
            //答案错误:测试账号特殊处理，减少相应雷数，重新判断答案是否正确
            if (isTest && currentPassLevel.getLevel().intValue() <= this.TEST_LEVEL_SUM.intValue()) {
                //减少雷数，重新判断答案是否正确
                if (this.TEST_MINE_SUM.intValue() > 0) {
                    //地雷格子集合
                    List<Integer> mineLatticeList = Arrays.stream(currentPassLevel.getMineLattice().split(",")).map(Integer::parseInt).collect(Collectors.toList());
                    //非地雷格子集合
                    List<Integer> nonMineLatticeList = Arrays.stream(currentPassLevel.getNonMineLattice().split(",")).map(Integer::parseInt).collect(Collectors.toList());
                    //雷数要大于少雷个数
                    if (mineLatticeList.size() > this.TEST_MINE_SUM.intValue()) {
                        for (int idx = 0; idx < this.TEST_MINE_SUM.intValue(); idx++) {
                            Integer mineLattice = mineLatticeList.get(0);
                            Integer nonMineLattice = nonMineLatticeList.get(0);
                            //雷区节点与非雷区节点互换
                            nonMineLatticeList.add(mineLattice);
                            mineLatticeList.remove(mineLattice);
                            mineLatticeList.add(nonMineLattice);
                            nonMineLatticeList.remove(nonMineLattice);
                        }
                        currentPassLevel.setMineLattice(StringUtils.join(mineLatticeList, ","));
                        currentPassLevel.setNonMineLattice(StringUtils.join(nonMineLatticeList, ","));
                        if (currentPassLevel.getNonMineLattice().contains(request.getSelectLattice().toString())) {
                            isAnswerCorrect = true;
                        }
                    }
                }
            }
        }
        //保存本关结果
        if (isAnswerCorrect) {
            currentPassLevel.setPassState(PassStateEnum.YES.getCode());
            response.setPrizeState(PrizeStateEnum.YES.getCode());
        } else {
            currentPassLevel.setPassState(PassStateEnum.NO.getCode());
            response.setPrizeState(PrizeStateEnum.NO.getCode());
        }
        currentPassLevel.setSelectLattice(request.getSelectLattice());
        currentPassLevel.setUpdateDate(new Date());
        currentPassLevel.setUpdateBy(name);
        mineUserActivityPassLevelMapper.updateById(currentPassLevel);
        //最终闯关结果：判断是否通关，如果通关则领取最终奖励，不通过则进入下一关
        boolean isPrize = true;
        if (isAnswerCorrect) {
            //正确:判断本关是否是最后一关，如果是最后一关直接发奖励，否则进入下一关
            if (!this.LEVEL_SUM.equals(currentPassLevel.getLevel())) {
                //不是最后一关，则进入下一关
                isPrize = false;
                Integer level = currentPassLevel.getLevel() + 1;
                MineUserActivityPassLevel nextUserActivityPassLevel = new MineUserActivityPassLevel();
                nextUserActivityPassLevel.setActivityId(mineUserActivity.getId());
                nextUserActivityPassLevel.setLevel(level);
                nextUserActivityPassLevel.setCreateDate(new Date());
                nextUserActivityPassLevel.setCreateBy(name);
                nextUserActivityPassLevel.setUserId(userId);
                nextUserActivityPassLevel.setPassState(PassStateEnum.INIT.getCode());
                int total = this.getTotalByLevel(level);
                int mineCount = this.getMineCountByLevel(level);
                RandomLevelItem randomLevelItem = this.randomExtract(total, mineCount);
                nextUserActivityPassLevel.setMineLattice(randomLevelItem.getMineLattice());
                nextUserActivityPassLevel.setNonMineLattice(randomLevelItem.getNonMineLattice());
                mineUserActivityPassLevelMapper.insert(nextUserActivityPassLevel);
            } else {
                response.setPrizeState(PrizeStateEnum.OVER.getCode());
            }
        } else {
            //错误：获取保底奖励
            MineGiftProductDTO mineGiftProductDTO = this.getMineRandomProduct();
            if (mineGiftProductDTO == null) {
                throw BizServerException.of(CommonBizCode.SYS_ERROR_MINE_OPEN);
            }
            Integer giftProductId = mineGiftProductDTO.getGiftProductId();
            giftProduct = giftProductPlusMapper.selectById(giftProductId);
            if (giftProduct == null) {
                throw BizServerException.of(CommonBizCode.PRODUCT_NOT_FOUND);
            }
        }
        //返回当前关答案信息
        //选中格子
        response.setSelectLattice(currentPassLevel.getSelectLattice());
        //地雷格子集合
        List<Integer> mineLatticeList = Arrays.stream(currentPassLevel.getMineLattice().split(",")).map(Integer::parseInt).collect(Collectors.toList());
        //非地雷格子集合
        List<Integer> nonMineLatticeList = Arrays.stream(currentPassLevel.getNonMineLattice().split(",")).map(Integer::parseInt).collect(Collectors.toList());
        response.setMineLattice(mineLatticeList);
        response.setNonMineLattice(nonMineLatticeList);
        if (isPrize) {
            //奖品入背包
            UserPlus player = userService.get(userId);
            this.record(player, giftProduct);
            //记录用户奖励信息
            this.saveMineUserPrize(mineUserActivity.getUserId(), mineUserActivity.getId(),
                    player.getUserName(), giftProduct.getId(), giftProduct.getName(), giftProduct.getPrice(), mineUserActivity.getPayPrice());
            //设置活动状态为已结束
            mineUserActivity.setFinishState(YesOrNoEnum.YES.getCode());
            mineUserActivity.setUpdateDate(new Date());
            mineUserActivity.setUpdateBy(name);
            mineUserActivityMapper.updateById(mineUserActivity);
            //返回奖品详细信息
            response.setGiftProductName(giftProduct.getName());
            response.setGiftProductImg(giftProduct.getImg());
            response.setGiftProductPrice(giftProduct.getPrice());
            response.setExteriorName(giftProduct.getExteriorName());
        }
        return response;
    }

    /**
     * 根据关数获取总数
     *
     * @param level
     * @return
     */
    private int getTotalByLevel(int level) {
        int total = 0;
        if (level == 1) {
            total = 7;
        } else if (level == 2) {
            total = 6;
        } else if (level == 3) {
            total = 5;
        } else if (level == 4) {
            total = 4;
        } else if (level == 5) {
            total = 3;
        } else if (level == 6) {
            total = 2;
        }
        return total;
    }

    /**
     * 根据关数获取总地雷数
     *
     * @param level
     * @return
     */
    private int getMineCountByLevel(int level) {
        int mineCount = 0;
        if (level == 1) {
            mineCount = 3;
        } else if (level == 2) {
            mineCount = 3;
        } else if (level == 3) {
            mineCount = 3;
        } else if (level == 4) {
            mineCount = 2;
        } else if (level == 5) {
            mineCount = 2;
        } else if (level == 6) {
            mineCount = 1;
        }
        return mineCount;
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
        userBalance.setRemark("Stimulating Minefield");
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalancePlusMapper.insert(userBalance);
        return userBalance;
    }

    /**
     * 奖品入背包
     *
     * @param player
     * @param giftProduct
     */
    private void record(UserPlus player, GiftProductPlus giftProduct) {
        //背包流水记录
        int recordId = userMessageRecordService.add(player.getId(), "Stimulating Minefield", "IN");
        //增加用户背包信息
        UserMessagePlus message = new UserMessagePlus();
        message.setGameName("Stimulating Minefield");
        message.setGiftProductId(giftProduct.getId());
        message.setMoney(giftProduct.getPrice());
        message.setDrawDare(new Date());
        message.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
        message.setGameName("Stimulating Minefield");
        message.setImg(giftProduct.getImg());
        message.setGiftType("Stimulating Minefield");
        message.setUserId(player.getId());
        message.setProductName(giftProduct.getName());
        message.setGiftStatus("0");
        message.setFromSource(BackpackFromSourceConstant.MINE);
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
        userMessageGift.setGameName("Stimulating Minefield");
        userMessageGift.setGiftProductName(giftProduct.getName());
        userMessageGift.setGiftType("Stimulating Minefield");
        userMessageGiftService.insert(userMessageGift);

        //增加用户的中奖记录
        UserPrizePlus userPrize = new UserPrizePlus();
        userPrize.setUserId(player.getId());
        userPrize.setUserNameQ(player.getName());
        userPrize.setUserName(player.getName());
        userPrize.setGiftId(0);
        userPrize.setGiftProductId(giftProduct.getId());
        userPrize.setGiftProductName(giftProduct.getName());
        userPrize.setGiftType("Stimulating Minefield");
        userPrize.setGiftName("Stimulating Minefield");
        userPrize.setGiftProductImg(giftProduct.getImg());
        userPrize.setPrice(giftProduct.getPrice());
        userPrize.setGiftGradeG(String.valueOf(giftProduct.getOutProbability()));
        userPrize.setGameName("CSGO");
        userPrize.setCt(new Date());
        userPrizeService.insert(userPrize);
        //扣除库存
        mineJackpotService.updateMineJackpot(giftProduct.getPrice().multiply(new BigDecimal(-1)), player);
    }

    /**
     * 获取或者创建活动信息
     *
     * @param userId
     * @param name
     * @return
     */
    private MineUserActivity getActivityInfo(int userId, String name) {
        LambdaQueryWrapper<MineUserActivity> activityWrapper = new LambdaQueryWrapper<>();
        activityWrapper.eq(MineUserActivity::getUserId, userId);
        activityWrapper.eq(MineUserActivity::getFinishState, YesOrNoEnum.NO.getCode());
        MineUserActivity mineUserActivity = mineUserActivityMapper.selectOne(activityWrapper);
        if (mineUserActivity == null) {
            mineUserActivity = new MineUserActivity();
            mineUserActivity.setPayState(YesOrNoEnum.NO.getCode());
            mineUserActivity.setFinishState(YesOrNoEnum.NO.getCode());
            mineUserActivity.setUserId(userId);
            mineUserActivity.setCreateBy(name);
            mineUserActivity.setCreateDate(new Date());
            mineUserActivityMapper.insert(mineUserActivity);
        }
        return mineUserActivity;
    }

    /**
     * 获取活动信息
     *
     * @param userId
     * @return
     */
    private MineUserActivity getActivityInfo(int userId) {
        LambdaQueryWrapper<MineUserActivity> activityWrapper = new LambdaQueryWrapper<>();
        activityWrapper.eq(MineUserActivity::getUserId, userId);
        activityWrapper.eq(MineUserActivity::getFinishState, YesOrNoEnum.NO.getCode());
        MineUserActivity mineUserActivity = mineUserActivityMapper.selectOne(activityWrapper);
        return mineUserActivity;
    }

    /**
     * 获取闯关信息，闯关信息不存在则创建
     *
     * @param userId
     * @param name
     * @param activityId
     * @return
     */
    private List<MineUserActivityPassLevelItem> getPassLevelItems(int userId, String name, int activityId) {
        List<MineUserActivityPassLevelItem> passLevelItems = new ArrayList<>();
        LambdaQueryWrapper<MineUserActivityPassLevel> activityPassLevelWrapper = new LambdaQueryWrapper<>();
        activityPassLevelWrapper.eq(MineUserActivityPassLevel::getUserId, userId);
        activityPassLevelWrapper.eq(MineUserActivityPassLevel::getActivityId, activityId);
        List<MineUserActivityPassLevel> activityPassLevelList = mineUserActivityPassLevelMapper.selectList(activityPassLevelWrapper);
        MineUserActivityPassLevelItem passLevelItem = new MineUserActivityPassLevelItem();
        if (CollectionUtils.isEmpty(activityPassLevelList)) {
            RandomLevelItem randomLevelItem = this.randomExtract(7, 3);
            //保存初始化闯关信息
            MineUserActivityPassLevel mineUserActivityPassLevel = new MineUserActivityPassLevel();
            mineUserActivityPassLevel.setActivityId(activityId);
            mineUserActivityPassLevel.setUserId(userId);
            mineUserActivityPassLevel.setLevel(1);
            mineUserActivityPassLevel.setPassState(PassStateEnum.INIT.getCode());
            mineUserActivityPassLevel.setMineLattice(randomLevelItem.getMineLattice());
            mineUserActivityPassLevel.setNonMineLattice(randomLevelItem.getNonMineLattice());
            mineUserActivityPassLevel.setCreateBy(name);
            mineUserActivityPassLevel.setCreateDate(new Date());
            mineUserActivityPassLevelMapper.insert(mineUserActivityPassLevel);
            //返回闯关信息
            passLevelItem.setId(mineUserActivityPassLevel.getId());
            passLevelItem.setLevel(mineUserActivityPassLevel.getLevel());
            passLevelItem.setPassState(mineUserActivityPassLevel.getPassState());
        } else {
            if (activityPassLevelList.size() > 1) {
                throw new ServiceErrorException("活动已经进行中，不能刷新");
            }
            MineUserActivityPassLevel mineUserActivityPassLevel = activityPassLevelList.get(0);
            //返回闯关信息
            passLevelItem.setId(mineUserActivityPassLevel.getId());
            passLevelItem.setLevel(mineUserActivityPassLevel.getLevel());
            passLevelItem.setPassState(mineUserActivityPassLevel.getPassState());
        }
        passLevelItems.add(passLevelItem);
        return passLevelItems;
    }

    /**
     * 获取闯关信息
     *
     * @param userId
     * @param activityId
     * @return
     */
    private List<MineUserActivityPassLevelItem> getPassLevelItems(int userId, int activityId) {
        List<MineUserActivityPassLevelItem> passLevelItemList = new ArrayList<>();
        LambdaQueryWrapper<MineUserActivityPassLevel> activityPassLevelWrapper = new LambdaQueryWrapper<>();
        activityPassLevelWrapper.eq(MineUserActivityPassLevel::getUserId, userId);
        activityPassLevelWrapper.eq(MineUserActivityPassLevel::getActivityId, activityId);
        List<MineUserActivityPassLevel> activityPassLevelList = mineUserActivityPassLevelMapper.selectList(activityPassLevelWrapper);
        if (CollectionUtils.isEmpty(activityPassLevelList)) {
            log.error("活动异常，请联系管理员");
            throw BizClientException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        } else {
            for (MineUserActivityPassLevel mineUserActivityPassLevel : activityPassLevelList) {
                MineUserActivityPassLevelItem passLevelItem = new MineUserActivityPassLevelItem();
                passLevelItem.setId(mineUserActivityPassLevel.getId());
                passLevelItem.setLevel(mineUserActivityPassLevel.getLevel());
                passLevelItem.setMineLattice(Arrays.stream(mineUserActivityPassLevel.getMineLattice().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
                passLevelItem.setNonMineLattice(Arrays.stream(mineUserActivityPassLevel.getNonMineLattice().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
                passLevelItem.setPassState(mineUserActivityPassLevel.getPassState());
                passLevelItem.setSelectLattice(mineUserActivityPassLevel.getSelectLattice());
                passLevelItemList.add(passLevelItem);
            }
            //按关数排序
            passLevelItemList = passLevelItemList.stream().sorted(Comparator.comparing(MineUserActivityPassLevelItem::getLevel)).collect(Collectors.toList());
            //清除当前活动层结果
            passLevelItemList.get(passLevelItemList.size() - 1).setNonMineLattice(null);
            passLevelItemList.get(passLevelItemList.size() - 1).setMineLattice(null);
        }
        return passLevelItemList;
    }

    /**
     * 获取闯关列表
     *
     * @param userId
     * @param activityId
     * @return
     */
    private List<MineUserActivityPassLevel> getMineUserActivityPassLevelList(int userId, int activityId) {
        LambdaQueryWrapper<MineUserActivityPassLevel> activityPassLevelWrapper = new LambdaQueryWrapper<>();
        activityPassLevelWrapper.eq(MineUserActivityPassLevel::getUserId, userId);
        activityPassLevelWrapper.eq(MineUserActivityPassLevel::getActivityId, activityId);
        List<MineUserActivityPassLevel> activityPassLevelList = mineUserActivityPassLevelMapper.selectList(activityPassLevelWrapper);
        //按关数升序排序
        activityPassLevelList = activityPassLevelList.stream().sorted(Comparator.comparing(MineUserActivityPassLevel::getLevel)).collect(Collectors.toList());
        return activityPassLevelList;
    }

    /**
     * 随机获取地雷信息
     *
     * @param total     格子总数
     * @param mineCount 地雷数量
     * @return
     */
    private RandomLevelItem randomExtract(int total, int mineCount) {
        if (mineCount >= total) {
            log.error("地雷数量不能大于等于格子总数");
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        RandomLevelItem item = new RandomLevelItem();
        //格子集合
        List<Integer> latticeList = new ArrayList<>();
        //地雷集合
        List<Integer> mineLatticeList = new ArrayList<>();
        for (int idx = 1; idx <= total; idx++) {
            latticeList.add(idx);
        }
        //随机获取地雷
        for (int idx = 1; idx <= mineCount; idx++) {
            Integer currentLattice = latticeList.get(new Random().nextInt(latticeList.size()));
            mineLatticeList.add(currentLattice);
            latticeList.remove(currentLattice);
        }
        item.setMineLattice(StringUtils.join(mineLatticeList, ","));
        item.setNonMineLattice(StringUtils.join(latticeList, ","));
        return item;
    }

    /**
     * 获取每关奖品信息,奖品信息不存在则自动创建
     *
     * @param userId
     * @param activityId
     * @param minPrice
     * @param maxPrice
     * @return
     */
    private List<MineUserActivityPrizeItem> getUserConfigPrizeItems(int userId, String name, int activityId, BigDecimal minPrice, BigDecimal maxPrice) {
        List<MineUserActivityPrizeItem> prizeList = new ArrayList<>();
        //第一层
        minPrice = (minPrice == null ? this.ONE_LEVEL_PRICE_MIN : minPrice);
        maxPrice = (maxPrice == null ? this.ONE_LEVEL_PRICE_MAX : maxPrice);
        BigDecimal onePrice = this.makeRandom(minPrice.floatValue(), maxPrice.floatValue(), 2);
        MineGiftProductDTO oneMineGiftProductDTO = giftProductPlusMapper.getGiftProductByPrice(onePrice);
        //第二层
        BigDecimal twoPrice = this.makeRandom(onePrice.multiply(this.TWO_LEVEL_RATIO_MIN).setScale(2, BigDecimal.ROUND_DOWN).floatValue(),
                onePrice.multiply(this.TWO_LEVEL_RATIO_MAX).setScale(2, BigDecimal.ROUND_DOWN).floatValue(), 2);
        MineGiftProductDTO twoMineGiftProductDTO = giftProductPlusMapper.getGiftProductByPrice(twoPrice);
        //第三层
        BigDecimal threePrice = this.makeRandom(onePrice.multiply(this.THREE_LEVEL_RATIO_MIN).setScale(2, BigDecimal.ROUND_DOWN).floatValue(),
                onePrice.multiply(this.THREE_LEVEL_RATIO_MAX).setScale(2, BigDecimal.ROUND_DOWN).floatValue(), 2);
        MineGiftProductDTO threeMineGiftProductDTO = giftProductPlusMapper.getGiftProductByPrice(threePrice);
        //第四层
        BigDecimal fourPrice = this.makeRandom(threePrice.multiply(this.FOUR_LEVEL_RATIO_MIN).setScale(2, BigDecimal.ROUND_DOWN).floatValue(),
                threePrice.multiply(this.FOUR_LEVEL_RATIO_MAX).setScale(2, BigDecimal.ROUND_DOWN).floatValue(), 2);
        MineGiftProductDTO fourMineGiftProductDTO = giftProductPlusMapper.getGiftProductByPrice(fourPrice);

        //第五层
        BigDecimal fivePrice = this.makeRandom(fourPrice.multiply(this.FIVE_LEVEL_RATIO_MIN).setScale(2, BigDecimal.ROUND_DOWN).floatValue(),
                fourPrice.multiply(this.FIVE_LEVEL_RATIO_MAX).setScale(2, BigDecimal.ROUND_DOWN).floatValue(), 2);
        MineGiftProductDTO fiveMineGiftProductDTO = giftProductPlusMapper.getGiftProductByPrice(fivePrice);

        //第六层
        BigDecimal sixPrice = this.makeRandom(fivePrice.multiply(this.SIX_LEVEL_RATIO_MIN).setScale(2, BigDecimal.ROUND_DOWN).floatValue(),
                fivePrice.multiply(this.SIX_LEVEL_RATIO_MAX).setScale(2, BigDecimal.ROUND_DOWN).floatValue(), 2);
        MineGiftProductDTO sixMineGiftProductDTO = giftProductPlusMapper.getGiftProductByPrice(sixPrice);

        MineUserActivityPrizeItem oneActivityPrizeItem = new MineUserActivityPrizeItem();
        oneActivityPrizeItem.setLevel(1);
        oneActivityPrizeItem.setGiftProductId(oneMineGiftProductDTO.getGiftProductId());
        oneActivityPrizeItem.setGiftProductName(oneMineGiftProductDTO.getGiftProductName());
        oneActivityPrizeItem.setGiftProductImg(oneMineGiftProductDTO.getGiftProductImg());
        oneActivityPrizeItem.setGiftProductPrice(oneMineGiftProductDTO.getGiftProductPrice());
        oneActivityPrizeItem.setExteriorName(oneMineGiftProductDTO.getExteriorName());
        prizeList.add(oneActivityPrizeItem);

        MineUserActivityPrizeItem twoActivityPrizeItem = new MineUserActivityPrizeItem();
        twoActivityPrizeItem.setLevel(2);
        twoActivityPrizeItem.setGiftProductId(twoMineGiftProductDTO.getGiftProductId());
        twoActivityPrizeItem.setGiftProductName(twoMineGiftProductDTO.getGiftProductName());
        twoActivityPrizeItem.setGiftProductImg(twoMineGiftProductDTO.getGiftProductImg());
        twoActivityPrizeItem.setGiftProductPrice(twoMineGiftProductDTO.getGiftProductPrice());
        twoActivityPrizeItem.setExteriorName(twoMineGiftProductDTO.getExteriorName());
        prizeList.add(twoActivityPrizeItem);

        MineUserActivityPrizeItem threeActivityPrizeItem = new MineUserActivityPrizeItem();
        threeActivityPrizeItem.setLevel(3);
        threeActivityPrizeItem.setGiftProductId(threeMineGiftProductDTO.getGiftProductId());
        threeActivityPrizeItem.setGiftProductName(threeMineGiftProductDTO.getGiftProductName());
        threeActivityPrizeItem.setGiftProductImg(threeMineGiftProductDTO.getGiftProductImg());
        threeActivityPrizeItem.setGiftProductPrice(threeMineGiftProductDTO.getGiftProductPrice());
        threeActivityPrizeItem.setExteriorName(threeMineGiftProductDTO.getExteriorName());
        prizeList.add(threeActivityPrizeItem);

        MineUserActivityPrizeItem fourActivityPrizeItem = new MineUserActivityPrizeItem();
        fourActivityPrizeItem.setLevel(4);
        fourActivityPrizeItem.setGiftProductId(fourMineGiftProductDTO.getGiftProductId());
        fourActivityPrizeItem.setGiftProductName(fourMineGiftProductDTO.getGiftProductName());
        fourActivityPrizeItem.setGiftProductImg(fourMineGiftProductDTO.getGiftProductImg());
        fourActivityPrizeItem.setGiftProductPrice(fourMineGiftProductDTO.getGiftProductPrice());
        fourActivityPrizeItem.setExteriorName(fourMineGiftProductDTO.getExteriorName());
        prizeList.add(fourActivityPrizeItem);

        MineUserActivityPrizeItem fiveActivityPrizeItem = new MineUserActivityPrizeItem();
        fiveActivityPrizeItem.setLevel(5);
        fiveActivityPrizeItem.setGiftProductId(fiveMineGiftProductDTO.getGiftProductId());
        fiveActivityPrizeItem.setGiftProductName(fiveMineGiftProductDTO.getGiftProductName());
        fiveActivityPrizeItem.setGiftProductImg(fiveMineGiftProductDTO.getGiftProductImg());
        fiveActivityPrizeItem.setGiftProductPrice(fiveMineGiftProductDTO.getGiftProductPrice());
        fiveActivityPrizeItem.setExteriorName(fiveMineGiftProductDTO.getExteriorName());
        prizeList.add(fiveActivityPrizeItem);

        MineUserActivityPrizeItem sixActivityPrizeItem = new MineUserActivityPrizeItem();
        sixActivityPrizeItem.setLevel(6);
        sixActivityPrizeItem.setGiftProductId(sixMineGiftProductDTO.getGiftProductId());
        sixActivityPrizeItem.setGiftProductName(sixMineGiftProductDTO.getGiftProductName());
        sixActivityPrizeItem.setGiftProductImg(sixMineGiftProductDTO.getGiftProductImg());
        sixActivityPrizeItem.setGiftProductPrice(sixMineGiftProductDTO.getGiftProductPrice());
        sixActivityPrizeItem.setExteriorName(sixMineGiftProductDTO.getExteriorName());
        prizeList.add(sixActivityPrizeItem);
        //保存用户每关奖品记录
        this.saveUserActivityPrize(userId, name, activityId, prizeList);
        return prizeList;
    }

    /**
     * 获取活动每关奖品信息
     *
     * @param userId
     * @param activityId
     * @return
     */
    private List<MineUserActivityPrizeItem> getPrizeItems(int userId, int activityId) {
        List<MineUserActivityPrizeItem> prizeList = new ArrayList<>();
        LambdaQueryWrapper<MineUserActivityPrize> activityPrizeWrapper = new LambdaQueryWrapper<>();
        activityPrizeWrapper.eq(MineUserActivityPrize::getUserId, userId);
        activityPrizeWrapper.eq(MineUserActivityPrize::getActivityId, activityId);
        List<MineUserActivityPrize> activityPrizeList = mineUserActivityPrizeService.list(activityPrizeWrapper);
        if (CollectionUtils.isEmpty(activityPrizeList)) {
            log.error("活动异常，请联系管理员");
            throw BizClientException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        } else {
            //按关数排序
            activityPrizeList = activityPrizeList.stream().sorted(Comparator.comparing(MineUserActivityPrize::getLevel)).collect(Collectors.toList());
            List<Integer> giftProductIdList = activityPrizeList.stream().map(MineUserActivityPrize::getGiftProductId).collect(Collectors.toList());
            List<GiftProductPlus> giftProductList = giftProductPlusMapper.findByIds(giftProductIdList);
            if (CollectionUtils.isEmpty(giftProductList)) {
                log.error("活动异常，请联系管理员");
                throw BizClientException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
            }
            for (MineUserActivityPrize mineUserActivityPrize : activityPrizeList) {
                GiftProductPlus giftProductPlus = giftProductList.stream().filter(giftProduct -> giftProduct.getId().equals(mineUserActivityPrize.getGiftProductId())).findFirst().orElse(null);
                MineUserActivityPrizeItem mineUserActivityPrizeItem = new MineUserActivityPrizeItem();
                mineUserActivityPrizeItem.setLevel(mineUserActivityPrize.getLevel());
                mineUserActivityPrizeItem.setGiftProductId(giftProductPlus.getId());
                mineUserActivityPrizeItem.setGiftProductName(giftProductPlus.getName());
                mineUserActivityPrizeItem.setGiftProductImg(giftProductPlus.getImg());
                mineUserActivityPrizeItem.setGiftProductPrice(giftProductPlus.getPrice());
                mineUserActivityPrizeItem.setExteriorName(giftProductPlus.getExteriorName());
                prizeList.add(mineUserActivityPrizeItem);
            }
        }
        return prizeList;
    }

    /**
     * 保存用户每关奖品记录
     *
     * @param userId
     * @param name
     * @param activityId
     * @param prizeList
     */
    private void saveUserActivityPrize(int userId, String name, int activityId, List<MineUserActivityPrizeItem> prizeList) {
        LambdaQueryWrapper<MineUserActivityPrize> activityPrizeWrapper = new LambdaQueryWrapper<>();
        activityPrizeWrapper.eq(MineUserActivityPrize::getUserId, userId);
        activityPrizeWrapper.eq(MineUserActivityPrize::getActivityId, activityId);
        List<MineUserActivityPrize> activityPrizeList = mineUserActivityPrizeService.list(activityPrizeWrapper);
        if (CollectionUtils.isEmpty(activityPrizeList)) {
            activityPrizeList = new ArrayList<>();
            for (MineUserActivityPrizeItem item : prizeList) {
                MineUserActivityPrize mineUserActivityPrize = new MineUserActivityPrize();
                mineUserActivityPrize.setActivityId(activityId);
                mineUserActivityPrize.setUserId(userId);
                mineUserActivityPrize.setLevel(item.getLevel());
                mineUserActivityPrize.setGiftProductId(item.getGiftProductId());
                mineUserActivityPrize.setCreateBy(name);
                mineUserActivityPrize.setCreateDate(new Date());
                activityPrizeList.add(mineUserActivityPrize);
            }
            mineUserActivityPrizeService.saveBatch(activityPrizeList);
        } else {
            for (MineUserActivityPrize mineUserActivityPrize : activityPrizeList) {
                for (MineUserActivityPrizeItem item : prizeList) {
                    if (mineUserActivityPrize.getLevel().equals(item.getLevel())) {
                        mineUserActivityPrize.setGiftProductId(item.getGiftProductId());
                        mineUserActivityPrize.setUpdateBy(name);
                        mineUserActivityPrize.setUpdateDate(new Date());
                        break;
                    }
                }
            }
            mineUserActivityPrizeService.updateBatchById(activityPrizeList);
        }
    }

    /**
     * 获取货币消耗金额
     *
     * @param prizeItems
     * @return
     */
    private BigDecimal getExtractAmount(List<MineUserActivityPrizeItem> prizeItems) {
        //第一层
        MineUserActivityPrizeItem oneItem = prizeItems.get(0);
        //第二层
        MineUserActivityPrizeItem twoItem = prizeItems.get(1);
        //支付货币公式：（A+B）/2.13
        BigDecimal extractAmount = oneItem.getGiftProductPrice().add(twoItem.getGiftProductPrice()).divide(this.PAY_RATIO, 2, BigDecimal.ROUND_DOWN);
        return extractAmount;
    }

    /**
     * 获取指定返回随机价格
     *
     * @return
     */
    private MineGiftProductDTO getMineRandomProduct() {
        BigDecimal minPrice = new BigDecimal(0.01);
        BigDecimal maxPrice = new BigDecimal(0.05);
        MineGiftProductDTO mineGiftProductDTO = null;
        int searchCount = 0;
        //5次还未获取到饰品，则跳出查找
        while (mineGiftProductDTO == null) {
            if (searchCount >= 5) {
                break;
            }
            //随机价格
            BigDecimal randomPrice = this.makeRandom(minPrice.floatValue(), maxPrice.floatValue(), 2);
            mineGiftProductDTO = giftProductPlusMapper.getGiftProductByPrice(randomPrice);
            searchCount++;
        }
        return mineGiftProductDTO;
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
     * 获取用户奖励价格区间
     *
     * @param userId
     * @return
     */
    private MineUserConfigPrice getMineUserConfigPrice(Integer userId) {
        LambdaQueryWrapper<MineUserConfigPrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MineUserConfigPrice::getUserId, userId);
        List<MineUserConfigPrice> activityPassLevelList = mineUserConfigPriceMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(activityPassLevelList)) {
            return null;
        } else {
            return activityPassLevelList.get(0);
        }
    }

    /**
     * 删除用户奖励价格区间
     *
     * @param userId
     * @return
     */
    private void deleteMineUserConfigPrice(Integer userId) {
        LambdaQueryWrapper<MineUserConfigPrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MineUserConfigPrice::getUserId, userId);
        mineUserConfigPriceMapper.delete(wrapper);
    }

    /**
     * 保存用户奖励信息
     *
     * @param userId
     * @param activityId
     * @param userName
     * @param giftProductId
     * @param prizeName
     * @param prizePrice
     * @param payPrice
     */
    public void saveMineUserPrize(Integer userId, Integer activityId, String userName,
                                  Integer giftProductId, String prizeName, BigDecimal prizePrice, BigDecimal payPrice) {
        MineUserPrize mineUserPrize = new MineUserPrize();
        mineUserPrize.setUserId(userId);
        mineUserPrize.setActivityId(activityId);
        mineUserPrize.setUserName(userName);
        mineUserPrize.setGiftProductId(giftProductId);
        mineUserPrize.setPrizeName(prizeName);
        mineUserPrize.setPrizePrice(prizePrice);
        mineUserPrize.setPayPrice(payPrice);
        mineUserPrize.setCreateDate(new Date());
        minePrizeMapper.insert(mineUserPrize);
    }
}
