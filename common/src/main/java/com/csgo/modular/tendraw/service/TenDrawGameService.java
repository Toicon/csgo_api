package com.csgo.modular.tendraw.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.modular.tendraw.config.TenDrawConfig;
import com.csgo.modular.tendraw.domain.TenDrawGameDO;
import com.csgo.modular.tendraw.domain.TenDrawProductDO;
import com.csgo.modular.tendraw.enums.TenDrawGameStatus;
import com.csgo.modular.tendraw.mapper.TenDrawGameMapper;
import com.csgo.modular.tendraw.mapper.TenDrawGameProductMapper;
import com.csgo.modular.tendraw.model.front.TenDrawPreviewVO;
import com.csgo.modular.tendraw.model.front.TenDrawProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenDrawGameService {

    private final TenDrawGameMapper tenDrawGameMapper;
    private final TenDrawGameProductMapper tenDrawGameProductMapper;

    @Transactional(rollbackFor = Exception.class)
    public void createGame(Integer userId, TenDrawPreviewVO data) {
        TenDrawGameDO game = BeanCopyUtil.notNullMap(data, TenDrawGameDO.class);
        game.setState(TenDrawGameStatus.ING.getCode());
        game.setUserId(userId);
        game.setSumAmount(data.getPayPrice());
        game.setPlayTimes(0);
        game.setMaxPlayTimes(TenDrawConfig.PLAY_MAX_TIMES);
        tenDrawGameMapper.insert(game);

        List<TenDrawProductVO> productList = data.getProductList();
        for (TenDrawProductVO item : productList) {
            TenDrawProductDO product = BeanCopyUtil.notNullMap(item, TenDrawProductDO.class);
            product.setGameId(game.getId());
            tenDrawGameProductMapper.insert(product);
        }
    }

    public void checkNotExistGame(Integer userId) {
        TenDrawGameDO lastUnFinishGame = tenDrawGameMapper.getLastUnFinishGame(userId);
        if (lastUnFinishGame != null) {
            throw BizClientException.of(CommonBizCode.GAME_UN_FINISH);
        }
    }

    public TenDrawGameDO getUnFinishGame(Integer userId) {
        return tenDrawGameMapper.getLastUnFinishGame(userId);
    }

}
