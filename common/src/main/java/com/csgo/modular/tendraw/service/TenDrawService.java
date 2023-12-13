package com.csgo.modular.tendraw.service;

import cn.hutool.core.collection.CollUtil;
import com.csgo.constants.CommonBizCode;
import com.csgo.constants.LockConstant;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.cache.RedisCacheLogic;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.service.LockService;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.modular.product.model.front.ProductSimpleVO;
import com.csgo.modular.tendraw.config.TenDrawConfig;
import com.csgo.modular.tendraw.domain.TenDrawBallDO;
import com.csgo.modular.tendraw.domain.TenDrawGameDO;
import com.csgo.modular.tendraw.domain.TenDrawGamePlayDO;
import com.csgo.modular.tendraw.domain.TenDrawProductDO;
import com.csgo.modular.tendraw.enums.TenDrawGameStatus;
import com.csgo.modular.tendraw.mapper.TenDrawBallMapper;
import com.csgo.modular.tendraw.mapper.TenDrawGameMapper;
import com.csgo.modular.tendraw.mapper.TenDrawGamePlayMapper;
import com.csgo.modular.tendraw.mapper.TenDrawGameProductMapper;
import com.csgo.modular.tendraw.model.TenDrawSettingVO;
import com.csgo.modular.tendraw.model.front.*;
import com.csgo.modular.tendraw.service.system.TenDrawJackpotService;
import com.csgo.modular.tendraw.service.system.TenDrawSystemConfigService;
import com.csgo.modular.tendraw.support.TenDrawBall;
import com.csgo.modular.tendraw.support.TenDrawPriceHelper;
import com.csgo.modular.user.logic.UserBalanceLogic;
import com.csgo.modular.user.logic.UserLogic;
import com.csgo.support.GlobalConstants;
import com.echo.framework.support.jackson.json.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenDrawService {

    private static final String DATA_TEN_DRAW_PREVIEW_KEY = "data:ten_draw:%s";

    private final RedisCacheLogic redisCacheLogic;
    private final LockService lockService;

    private final UserBalanceLogic userBalanceLogic;
    private final TenDrawProductService tenDrawProductService;

    private final TenDrawGameService tenDrawGameService;
    private final TenDrawBackpackService tenDrawBackpackService;
    private final TenDrawLotteryService tenDrawLotteryService;

    private final UserLogic userLogic;

    private final TenDrawBallMapper tenDrawBallMapper;
    private final TenDrawGameMapper tenDrawGameMapper;
    private final TenDrawGamePlayMapper tenDrawGamePlayMapper;
    private final TenDrawGameProductMapper tenDrawGameProductMapper;

    private final TenDrawJackpotService tenDrawJackpotService;
    private final TenDrawSystemConfigService tenDrawSystemConfigService;

    private String getPreviewKey(String uuid) {
        return String.format(DATA_TEN_DRAW_PREVIEW_KEY, uuid);
    }

    public TenDrawConfigVO getConfig() {
        List<TenDrawBallDO> list = tenDrawBallMapper.listAll();
        Integer boxMaxSize = tenDrawSystemConfigService.getBoxMaxSize();

        List<TenDrawBall> voList = BeanCopyUtil.mapList(list, TenDrawBall.class);

        TenDrawConfigVO vo = new TenDrawConfigVO();
        vo.setBallMaxSize(boxMaxSize);
        vo.setBallList(voList);
        return vo;
    }

    public TenDrawPreviewVO preview(TenDrawPreviewVM vm) {
        if (CollUtil.isEmpty(vm.getBallList())) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }

        TenDrawSettingVO setting = tenDrawSystemConfigService.getSetting();

        BigDecimal ballSumValue = calcBallSumValue(setting, vm.getBallList());
        TenDrawPriceHelper priceHelper = new TenDrawPriceHelper(setting, TenDrawConfig.PRODUCT_COUNT, ballSumValue);

        List<ProductSimpleVO> productList = tenDrawProductService.getRandomProduct(priceHelper.getPriceList());

        List<TenDrawProductVO> productVOList = BeanCopyUtil.mapList(productList, TenDrawProductVO.class);
        Map<Integer, Integer> colorMap = priceHelper.getColorMap();
        for (int i = 0; i < productVOList.size(); i++) {
            TenDrawProductVO item = productVOList.get(i);
            Integer color = colorMap.getOrDefault(i, 0);
            item.setColor(color);
        }
        productVOList.sort(Comparator.comparing(TenDrawProductVO::getProductPrice).reversed());

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        TenDrawPreviewVO vo = new TenDrawPreviewVO();
        vo.setUuid(uuid);

        vo.setPayPrice(priceHelper.getPayPrice());
        vo.setRetryPrice(priceHelper.getRetryPrice());

        vo.setBlueRate(priceHelper.getBlueRate());
        vo.setRedRate(priceHelper.getRedRate());
        vo.setYellowRate(priceHelper.getYellowRate());
        vo.setProductList(productVOList);

        String key = getPreviewKey(uuid);
        redisCacheLogic.set(key, vo, 3, TimeUnit.HOURS);
        return vo;
    }

    private BigDecimal calcBallSumValue(TenDrawSettingVO setting, List<TenDrawPreviewVM.Ball> ballList) {
        Integer ballMaxSize = setting.getBallMaxSize();
        Integer selectBallNum = ballList.stream().map(TenDrawPreviewVM.Ball::getNum).reduce(0, Integer::sum);
        if (selectBallNum == 0) {
            log.error("[十连魂球] 至少选择一个魂球");
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        if (selectBallNum > ballMaxSize) {
            log.error("[十连魂球] 魂球最大选择个数为:{}", ballMaxSize);
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }

        List<TenDrawBallDO> configList = tenDrawBallMapper.listAll();

        Map<Integer, TenDrawBallDO> configIdMap = configList.stream().collect(Collectors.toMap(TenDrawBallDO::getId, Function.identity()));

        BigDecimal sum = BigDecimal.ZERO;
        for (TenDrawPreviewVM.Ball ball : ballList) {
            if (ball.getNum() == null || ball.getNum() <= 0) {
                log.debug("[十连魂球] ballId:{} num value illegal", ball.getId());
                continue;
            }

            if (configIdMap.containsKey(ball.getId())) {
                TenDrawBallDO tenDrawBall = configIdMap.get(ball.getId());
                BigDecimal weight = tenDrawBall.getWeight().multiply(BigDecimal.valueOf(ball.getNum()));
                sum = sum.add(weight);
            }
        }
        if (BigDecimalUtil.lessEqualZero(sum)) {
            log.error("[十连魂球] 开箱魂球配置错误 sum:{}", sum);
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        return sum;
    }

    @Transactional(rollbackFor = Exception.class)
    public void pay(Integer userId, TenDrawPayVM vm) {
        Assert.notNull(userId, "userId must not be null");

        String key = getPreviewKey(vm.getUuid());
        TenDrawPreviewVO data = redisCacheLogic.get(key, TenDrawPreviewVO.class)
                .orElseThrow(() -> BizClientException.of(CommonBizCode.TEN_DRAW_DATA_INVALID));
        log.info("[十连支付] pay data:{}", JSON.toJSON(data));

        String lockKey = LockConstant.LOCK_USER + userId;
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 5, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }
            doPay(userId, data);
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

    private void doPay(Integer userId, TenDrawPreviewVO data) {
        tenDrawGameService.checkNotExistGame(userId);

        BigDecimal payPrice = data.getPayPrice();
        log.info("[十连支付] pay userId:{} payPrice:{}", userId, payPrice);

        UserPlus player = userLogic.loadUser(userId);
        doCost(player, payPrice);

        tenDrawGameService.createGame(userId, data);
    }

    private void doCost(UserPlus player, BigDecimal payPrice) {
        userBalanceLogic.cost(player.getId(), payPrice, "Arms Mall");
        BigDecimal rate;
        if (GlobalConstants.INTERNAL_USER_FLAG == player.getFlag()) {
            rate = new BigDecimal("0.9");
        } else {
            rate = new BigDecimal("0.65");
        }
        tenDrawJackpotService.addJackpot(player, payPrice, rate);
    }

    public LastTenDrawVO getUnFinishGame(Integer userId) {
        TenDrawGameDO game = tenDrawGameService.getUnFinishGame(userId);

        LastTenDrawVO vo = new LastTenDrawVO();
        vo.setExistGame(game != null);
        if (game != null) {
            BeanCopyUtil.mapTo(game, vo);
            vo.setGameId(game.getId());

            List<TenDrawProductDO> productList = tenDrawGameProductMapper.listByGameId(game.getId());
            List<TenDrawProductVO> productListVO = BeanCopyUtil.mapList(productList, TenDrawProductVO.class);
            productListVO.sort(Comparator.comparing(TenDrawProductVO::getProductPrice).reversed());

            List<TenDrawGamePlayDO> playList = tenDrawGamePlayMapper.listByGameId(game.getId());
            List<TenDrawGamePlayRewardVO> playListVO = BeanCopyUtil.mapList(playList, TenDrawGamePlayRewardVO.class);

            vo.setProductList(productListVO);
            vo.setRewardList(playListVO);
        }
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public TenDrawGamePlayVO play(Integer userId, @Valid TenDrawGamePlayVM vm) {
        Assert.notNull(userId, "userId must not be null");

        String lockKey = LockConstant.LOCK_USER + userId;
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 5, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }

            return doPlay(userId, vm);
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

    private TenDrawGamePlayVO doPlay(Integer userId, TenDrawGamePlayVM vm) {
        TenDrawGameDO game = tenDrawGameService.getUnFinishGame(userId);
        if (game == null) {
            throw BizClientException.of(CommonBizCode.GAME_NOT_FOUND);
        }
        if (TenDrawGameStatus.FINISHED.getCode().equals(game.getState())) {
            throw BizClientException.of(CommonBizCode.GAME_FINISH);
        }

        Integer playIndex = vm.getPlayIndex();
        Integer maxPlayTimes = game.getMaxPlayTimes();
        if (playIndex >= maxPlayTimes) {
            log.error("[十连play] 箱子编号过大 playIndex:{} maxPlayTimes:{}", playIndex, maxPlayTimes);
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }

        List<TenDrawGamePlayDO> playList = tenDrawGamePlayMapper.listByGameId(game.getId());
        Set<Integer> indexSet = playList.stream().map(TenDrawGamePlayDO::getPlayIndex).collect(Collectors.toSet());
        if (indexSet.contains(playIndex)) {
            log.error("[十连play] 该箱子已经选择过 playIndex:{} ", playIndex);
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }

        Integer playTimes = game.getPlayTimes();
        boolean firstGame = playTimes == 0;
        boolean lastGame = playTimes + 1 >= game.getMaxPlayTimes();

        UserPlus player = userLogic.loadUser(userId);

        log.info("[十连play] userId:{} gameId:{} playTimes:{} payPrice:{} retryPrice:{}",
                userId, game.getId(), playTimes, game.getPayPrice(), game.getRetryPrice());
        BigDecimal payPrice = BigDecimal.ZERO;
        if (!firstGame) {
            payPrice = game.getRetryPrice();
            doCost(player, payPrice);
        }
        int playCount = playList.size();
        Integer playNum = playCount + 1;
        game.setPlayTimes(playNum);
        game.setSumAmount(game.getSumAmount().add(payPrice));
        tenDrawGameMapper.updateById(game);

        // 最后一次
        TenDrawGamePlayDO last = null;
        if (playList.size() > 1) {
            playList.sort(Comparator.comparing(TenDrawGamePlayDO::getId).reversed());
        }
        if (CollUtil.isNotEmpty(playList)) {
            last = playList.get(0);
        }

        List<TenDrawProductDO> productList = tenDrawGameProductMapper.listByGameId(game.getId());
        TenDrawProductDO hitProduct = tenDrawLotteryService.draw(player, game, productList);
        tenDrawJackpotService.deductJackpot(player, hitProduct.getProductPrice());
        if (last != null) {
            // 返回上一次奖励
            tenDrawJackpotService.addJackpot(player, last.getRewardProductPrice(), null);
        }
        Integer rewardIndex = getRewardIndex(productList, hitProduct);

        TenDrawGamePlayDO entity = new TenDrawGamePlayDO();
        entity.setRewardIndex(rewardIndex);
        entity.setGameId(game.getId());
        entity.setUserId(userId);
        entity.setPlayPrice(payPrice);
        entity.setPlayNum(playNum);
        entity.setPlayIndex(playIndex);
        entity.setRewardProductId(hitProduct.getProductId());
        entity.setRewardProductName(hitProduct.getProductName());
        entity.setRewardProductImg(hitProduct.getProductImg());
        entity.setRewardProductPrice(hitProduct.getProductPrice());
        entity.setColor(hitProduct.getColor());
        tenDrawGamePlayMapper.insert(entity);

        if (lastGame) {
            tenDrawBackpackService.inBackpack(game, entity);
        }
        TenDrawGamePlayRewardVO rewardVO = BeanCopyUtil.map(entity, TenDrawGamePlayRewardVO.class);

        TenDrawGamePlayVO vo = BeanCopyUtil.notNullMap(game, TenDrawGamePlayVO.class);
        vo.setReward(rewardVO);
        vo.setLastGame(lastGame);
        return vo;
    }

    private Integer getRewardIndex(List<TenDrawProductDO> list, TenDrawProductDO hitProduct) {
        list.sort(Comparator.comparing(TenDrawProductDO::getProductPrice).reversed());
        for (int i = 0; i < list.size(); i++) {
            int index = i + 1;
            TenDrawProductDO entity = list.get(i);
            if (entity.getProductId().equals(hitProduct.getProductId())) {
                return index;
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void finish(Integer userId) {
        Assert.notNull(userId, "userId must not be null");

        String lockKey = LockConstant.LOCK_USER + userId;
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 5, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }
            doFinish(userId);
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

    private void doFinish(Integer userId) {
        TenDrawGameDO game = tenDrawGameService.getUnFinishGame(userId);
        if (game == null) {
            throw BizClientException.of(CommonBizCode.GAME_NOT_FOUND);
        }

        Integer playCount = tenDrawGamePlayMapper.countByGameId(game.getId());
        if (playCount == 0) {
            throw BizClientException.of(CommonBizCode.TEN_DRAW_UN_PLAY);
        }
        TenDrawGamePlayDO last = tenDrawGamePlayMapper.getLastByGameId(game.getId());
        tenDrawBackpackService.inBackpack(game, last);
    }

}
