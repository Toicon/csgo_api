package com.csgo.modular.bomb.service;

import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.modular.bomb.domain.NovBombGameDO;
import com.csgo.modular.bomb.domain.NovBombGamePlayDO;
import com.csgo.modular.bomb.domain.NovBombProductDO;
import com.csgo.modular.bomb.enums.NovBombGameStatus;
import com.csgo.modular.bomb.enums.NovBombPlayType;
import com.csgo.modular.bomb.mapper.NovBombGamePlayMapper;
import com.csgo.modular.bomb.mapper.NovBombGameProductMapper;
import com.csgo.modular.bomb.model.front.NovBombProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovBombPlayService {

    private final NovBombGamePlayMapper novBombGamePlayMapper;
    private final NovBombGameProductMapper novBombGameProductMapper;

    public void createPlay(NovBombGameDO game, NovBombPlayType playType, BigDecimal price, List<NovBombProductVO> productList) {
        NovBombGamePlayDO play = new NovBombGamePlayDO();
        play.setGameId(game.getId());
        play.setUserId(game.getUserId());
        play.setStatus(NovBombGameStatus.ING.getCode());
        play.setPlayType(playType.getCode());
        play.setPrice(price);
        novBombGamePlayMapper.insert(play);

        for (NovBombProductVO item : productList) {
            NovBombProductDO product = BeanCopyUtil.notNullMap(item, NovBombProductDO.class);
            product.setGameId(game.getId());
            product.setPlayId(play.getId());
            novBombGameProductMapper.insert(product);
        }
    }

    public NovBombGamePlayDO getLastUnFinishPlay(Integer userId) {
        return novBombGamePlayMapper.getLastUnFinishPlay(userId);
    }

}
