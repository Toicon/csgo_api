package com.csgo.modular.bomb.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.constants.LockConstant;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.service.LockService;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.modular.bomb.domain.NovBombGameDO;
import com.csgo.modular.bomb.domain.NovBombGamePlayDO;
import com.csgo.modular.bomb.domain.NovBombProductDO;
import com.csgo.modular.bomb.enums.NovBombGameStatus;
import com.csgo.modular.bomb.mapper.NovBombGameMapper;
import com.csgo.modular.bomb.mapper.NovBombGamePlayMapper;
import com.csgo.modular.bomb.mapper.NovBombGameProductMapper;
import com.csgo.modular.bomb.model.front.NovBombGamePlayVM;
import com.csgo.modular.bomb.model.front.NovBombGamePlayVO;
import com.csgo.modular.bomb.service.system.NovBombJackpotService;
import com.csgo.modular.user.logic.UserLogic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovBombDrawService {

    private final LockService lockService;

    private final UserLogic userLogic;

    private final NovBombGameMapper novBombGameMapper;
    private final NovBombGamePlayMapper novBombGamePlayMapper;
    private final NovBombGameProductMapper novBombGameProductMapper;

    private final NovBombJackpotService novBombJackpotService;

    private final NovBombPlayService novBombPlayService;
    private final NovBombLotteryService novBombLotteryService;

    @Transactional(rollbackFor = Exception.class)
    public NovBombGamePlayVO draw(Integer userId, @Valid NovBombGamePlayVM vm) {
        Assert.notNull(userId, "用户ID不能为空");

        String lockKey = LockConstant.LOCK_USER + userId;
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 6, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }
            return doDraw(userId, vm);
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

    private NovBombGamePlayVO doDraw(Integer userId, NovBombGamePlayVM vm) {
        NovBombGamePlayDO play = novBombPlayService.getLastUnFinishPlay(userId);
        if (play == null) {
            throw BizClientException.of(CommonBizCode.GAME_NOT_FOUND);
        }
        if (NovBombGameStatus.FINISHED.getCode().equals(play.getStatus())) {
            throw BizClientException.of(CommonBizCode.GAME_FINISH);
        }

        Integer chooseIndex = vm.getChooseIndex();
        if (chooseIndex < 0 || chooseIndex >= 5) {
            log.error("[模拟拆弹][play] 箱子编号过大 chooseIndex:{}", chooseIndex);
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }

        UserPlus player = userLogic.loadUser(userId);
        NovBombGameDO game = novBombGameMapper.selectById(play.getGameId());

        log.info("[模拟拆弹][play] userId:{} gameId:{} playTimes:{}", userId, play.getId(), game.getPlayTimes());
        Integer playTimes = game.getPlayTimes() + 1;
        game.setPlayTimes(playTimes);
        novBombGameMapper.updateById(game);

        List<NovBombProductDO> productList = novBombGameProductMapper.listByPlayId(play.getId());
        NovBombProductDO hitProduct = novBombLotteryService.draw(player, game, play, chooseIndex, productList);
        novBombJackpotService.deductJackpot(player, hitProduct.getProductPrice());

        play.setChooseIndex(chooseIndex);
        play.setStatus(NovBombGameStatus.FINISHED.getCode());
        play.setRewardDate(new Date());
        play.setRewardProductId(hitProduct.getProductId());
        play.setRewardProductName(hitProduct.getProductName());
        play.setRewardProductImg(hitProduct.getProductImg());
        play.setRewardProductPrice(hitProduct.getProductPrice());
        play.setRewardProductIndex(hitProduct.getProductIndex());
        play.setRewardProductExteriorName(hitProduct.getProductExteriorName());
        novBombGamePlayMapper.updateById(play);

        return BeanCopyUtil.notNullMap(play, NovBombGamePlayVO.class);
    }

}
