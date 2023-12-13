package com.csgo.modular.bomb.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.constants.LockConstant;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.service.LockService;
import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.modular.bomb.domain.NovBombConfigDO;
import com.csgo.modular.bomb.domain.NovBombGameDO;
import com.csgo.modular.bomb.domain.NovBombGamePlayDO;
import com.csgo.modular.bomb.enums.NovBombGameStatus;
import com.csgo.modular.bomb.enums.NovBombPlayType;
import com.csgo.modular.bomb.mapper.NovBombGamePlayMapper;
import com.csgo.modular.bomb.model.front.NovBombPayAgainVM;
import com.csgo.modular.bomb.model.front.NovBombPayVM;
import com.csgo.modular.bomb.model.front.NovBombPreviewVO;
import com.csgo.modular.bomb.model.front.NovBombProductVO;
import com.csgo.modular.bomb.service.system.NovBombJackpotService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovBombPayService {

    private final NovBombGamePlayMapper novBombGamePlayMapper;

    private final LockService lockService;

    private final UserLogic userLogic;
    private final UserBalanceLogic userBalanceLogic;

    private final NovBombGameService novBombGameService;
    private final NovBombJackpotService novBombJackpotService;

    private final NovBombPreviewService novBombPreviewService;
    private final NovBombPlayService novBombPlayService;
    private final NovBombProductService novBombProductService;
    private final NovBombConfigService novBombConfigService;

    @Transactional(rollbackFor = Exception.class)
    public void pay(Integer userId, NovBombPayVM vm) {
        String uuid = vm.getUuid();
        Assert.notNull(userId, "用户ID不能为空");

        log.info("[模拟拆弹支付] pay userId:{} uuid:{}", userId, uuid);
        String lockKey = LockConstant.LOCK_USER + userId;
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 6, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }
            doPay(userId, uuid);
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

    public void payAgain(Integer userId, NovBombPayAgainVM vm) {
        BigDecimal payPrice = vm.getPayPrice();
        Assert.notNull(userId, "用户ID不能为空");

        log.info("[模拟拆弹继续支付] pay userId:{} payPrice:{}", userId, payPrice);
        String lockKey = LockConstant.LOCK_USER + userId;
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 6, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }
            doPayAgain(userId, payPrice);
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

    private void doPay(Integer userId, String uuid) {
        NovBombGameDO lastUnFinishGame = novBombGameService.getUnFinishGame(userId);
        if (lastUnFinishGame != null) {
            throw BizClientException.of(CommonBizCode.GAME_UN_FINISH);
        }

        NovBombPreviewVO data = novBombPreviewService.getPreview(uuid);
        log.info("[模拟拆弹支付] pay data:{}", JSON.toJSON(data));

        UserPlus player = userLogic.loadUser(userId);
        BigDecimal payPrice = data.getPrice();

        userBalanceLogic.cost(player.getId(), payPrice, "Stimulating Minefield");
        addJackpot(player, payPrice);

        NovBombGameDO game = novBombGameService.createGame(userId, data);
        List<NovBombProductVO> productList = data.getProductList();
        novBombPlayService.createPlay(game, NovBombPlayType.FIRST, game.getPrice(), productList);
    }

    private void doPayAgain(Integer userId, BigDecimal payPrice) {
        NovBombGameDO lastUnFinishGame = novBombGameService.getUnFinishGame(userId);
        if (lastUnFinishGame == null) {
            throw BizClientException.of(CommonBizCode.GAME_NOT_FOUND);
        }
        NovBombGamePlayDO last = novBombGamePlayMapper.getLastByGameId(lastUnFinishGame.getId());
        if (last == null) {
            throw BizClientException.of(CommonBizCode.GAME_NOT_FOUND);
        }
        if (NovBombGameStatus.ING.getCode().equals(last.getStatus())) {
            log.error("未进行拆线，无法继续挑战。");
            throw BizClientException.of(CommonBizCode.COMMON_STATUS_INCORRECT);
        }

        boolean again = BigDecimalUtil.equalZero(payPrice);
        NovBombPlayType playType = again ? NovBombPlayType.AGAIN : NovBombPlayType.DIFFICULTY;
        if (again) {
            // 再次挑战
            if (BigDecimalUtil.lessThan(last.getRewardProductPrice(), lastUnFinishGame.getLowPrice())) {
                log.error(String.format("奖励金额小于%s，无法继续挑战。", lastUnFinishGame.getLowPrice()));
                throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
            }
            if (BigDecimalUtil.greaterThan(last.getRewardProductPrice(), lastUnFinishGame.getHighPrice())) {
                log.error(String.format("奖励金额高于%s，无法继续挑战。", lastUnFinishGame.getLowPrice()));
                throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
            }
        } else {
            // 增加难度
            if (BigDecimalUtil.lessThan(payPrice, lastUnFinishGame.getLowPrice())) {
                log.error(String.format("增加难度金额小于%s，无法增加难度。", lastUnFinishGame.getLowPrice()));
                throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
            }
            Integer difficultyTimes = novBombGamePlayMapper.countByGameIdAndPlayType(lastUnFinishGame.getId(), NovBombPlayType.DIFFICULTY);
            if (difficultyTimes >= lastUnFinishGame.getMaxTimes()) {
                throw BizClientException.of(CommonBizCode.NOV_BOMB_TIMES_LIMIT);
            }
        }

        UserPlus player = userLogic.loadUser(userId);
        if (BigDecimalUtil.greaterThanZero(payPrice)) {
            userBalanceLogic.cost(player.getId(), payPrice, "Stimulating Minefield");
        }

        BigDecimal newPlayPrice = last.getRewardProductPrice();
        if (BigDecimalUtil.greaterThanZero(payPrice)) {
            newPlayPrice = newPlayPrice.add(payPrice);
        }

        Integer configId = lastUnFinishGame.getConfigId();
        NovBombConfigDO config = novBombConfigService.loadConfig(configId);
        List<NovBombProductVO> productList = novBombProductService.getProduct(config, newPlayPrice);
        novBombPlayService.createPlay(lastUnFinishGame, playType, payPrice, productList);
        novBombGameService.addSumPrice(lastUnFinishGame, payPrice);

        addJackpot(player, newPlayPrice);
    }

    private void addJackpot(UserPlus player, BigDecimal payPrice) {
        BigDecimal rate = getRate(player);
        novBombJackpotService.addJackpot(player, payPrice, rate);
    }

    private BigDecimal getRate(UserPlus player) {
        BigDecimal rate;
        if (GlobalConstants.INTERNAL_USER_FLAG == player.getFlag()) {
            rate = null;
        } else {
            rate = new BigDecimal("0.9");
        }
        return rate;
    }

}
