package com.csgo.modular.bomb.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.constants.LockConstant;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.service.LockService;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.modular.bomb.domain.NovBombGameDO;
import com.csgo.modular.bomb.domain.NovBombGamePlayDO;
import com.csgo.modular.bomb.domain.NovBombProductDO;
import com.csgo.modular.bomb.enums.NovBombGameStatus;
import com.csgo.modular.bomb.enums.NovBombPlayType;
import com.csgo.modular.bomb.mapper.NovBombGameMapper;
import com.csgo.modular.bomb.mapper.NovBombGamePlayMapper;
import com.csgo.modular.bomb.mapper.NovBombGameProductMapper;
import com.csgo.modular.bomb.model.front.LastNovBombVO;
import com.csgo.modular.bomb.model.front.NovBombGamePlayVO;
import com.csgo.modular.bomb.model.front.NovBombPreviewVO;
import com.csgo.modular.bomb.model.front.NovBombProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovBombGameService {

    private final NovBombGameMapper novBombGameMapper;

    private final NovBombGamePlayMapper novBombGamePlayMapper;
    private final NovBombGameProductMapper novBombGameProductMapper;

    private final LockService lockService;
    private final NovBombBackpackService novBombBackpackService;

    @Transactional(rollbackFor = Exception.class)
    public NovBombGameDO createGame(Integer userId, NovBombPreviewVO data) {
        NovBombGameDO game = new NovBombGameDO();
        game.setUuid(data.getUuid());
        game.setConfigId(data.getConfigId());
        game.setStatus(NovBombGameStatus.ING.getCode());
        game.setUserId(userId);
        game.setPrice(data.getPrice());
        game.setSumAmount(data.getPrice());
        game.setPlayTimes(0);
        game.setLowPrice(data.getLowPrice());
        game.setHighPrice(data.getHighPrice());
        if (data.getMaxTimes() != null) {
            game.setMaxTimes(data.getMaxTimes());
        }
        novBombGameMapper.insert(game);

        return game;
    }

    public void addSumPrice(NovBombGameDO lastUnFinishGame, BigDecimal payPrice) {
        if (BigDecimalUtil.greaterThanZero(payPrice)) {
            BigDecimal sumAmount = lastUnFinishGame.getSumAmount() == null ? BigDecimal.ZERO : lastUnFinishGame.getSumAmount();

            lastUnFinishGame.setSumAmount(sumAmount);
            novBombGameMapper.updateById(lastUnFinishGame);
        }
    }

    public NovBombGameDO getUnFinishGame(Integer userId) {
        return novBombGameMapper.getLastUnFinishGame(userId);
    }

    public LastNovBombVO getUnFinishGameVo(Integer userId) {
        NovBombGameDO game = novBombGameMapper.getLastUnFinishGame(userId);
        LastNovBombVO vo = new LastNovBombVO();
        vo.setExistGame(game != null);
        if (game != null) {

            NovBombGamePlayDO play = novBombGamePlayMapper.getLastByUserId(userId, game.getId());
            NovBombGamePlayVO playVo = BeanCopyUtil.notNullMap(play, NovBombGamePlayVO.class);

            List<NovBombProductDO> productList = novBombGameProductMapper.listByPlayId(play.getId());
            List<NovBombProductVO> productListVo = BeanCopyUtil.mapList(productList, NovBombProductVO.class);

            Integer difficultyTimes = novBombGamePlayMapper.countByGameIdAndPlayType(game.getId(), NovBombPlayType.DIFFICULTY);

            vo.setConfigId(game.getConfigId());
            vo.setLowPrice(game.getLowPrice());
            vo.setHighPrice(game.getHighPrice());
            vo.setMaxTimes(game.getMaxTimes());
            vo.setDifficultyTimes(difficultyTimes);
            vo.setPlay(playVo);
            vo.setProductList(productListVo);
        }
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void finish(Integer userId) {
        Assert.notNull(userId, "用户ID不能为空");

        String lockKey = LockConstant.LOCK_USER + userId;
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 6, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }
            doFinish(userId);
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

    private void doFinish(Integer userId) {
        NovBombGameDO game = novBombGameMapper.getLastUnFinishGame(userId);
        if (game == null) {
            throw BizClientException.of(CommonBizCode.GAME_NOT_FOUND);
        }
        NovBombGamePlayDO last = novBombGamePlayMapper.getLastByGameId(game.getId());
        if (last == null || !NovBombGameStatus.FINISHED.getCode().equals(last.getStatus())) {
            throw BizClientException.of(CommonBizCode.COMMON_STATUS_INCORRECT);
        }

        novBombBackpackService.inBackpack(game, last);

        game.setStatus(NovBombGameStatus.FINISHED.getCode());
        game.setRewardProductId(last.getRewardProductId());
        game.setRewardProductName(last.getRewardProductName());
        game.setRewardProductImg(last.getRewardProductImg());
        game.setRewardProductPrice(last.getRewardProductPrice());
        game.setRewardProductIndex(last.getRewardProductId());
        game.setRewardProductExteriorName(last.getRewardProductExteriorName());
        game.setRewardDate(new Date());
        game.updateById();
    }

}
