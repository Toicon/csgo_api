package com.csgo.modular.bomb.service;

import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.modular.bomb.domain.NovBombGameDO;
import com.csgo.modular.bomb.domain.NovBombGamePlayDO;
import com.csgo.modular.bomb.mapper.NovBombGameMapper;
import com.csgo.modular.bomb.mapper.NovBombGamePlayMapper;
import com.csgo.modular.bomb.model.front.NovBombRewardVO;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovBombRewardService {

    private final NovBombGameMapper novBombGameMapper;
    private final NovBombGamePlayMapper novBombGamePlayMapper;

    private static final Integer MAX_SIZE = 50;

    public List<NovBombRewardVO> getLeaderboard(Integer num, Integer configId) {
        if (num > MAX_SIZE) {
            throw BizClientException.of(String.format("num should not be greater than %s", num));
        }
        List<NovBombGameDO> list = novBombGameMapper.getTodayLeaderboard(num, configId);
        return BeanCopyUtil.mapList(list, NovBombRewardVO.class);
    }

    public List<NovBombRewardVO> getRecent(Integer num, Integer configId) {
        if (num > MAX_SIZE) {
            throw BizClientException.of(String.format("num should not be greater than %s", num));
        }
        List<NovBombGameDO> list = novBombGameMapper.getRecent(num, configId);
        return BeanCopyUtil.mapList(list, NovBombRewardVO.class);
    }

    public List<NovBombRewardVO> getUnFinishHistory(Integer num, Integer userId) {
        if (userId == null) {
            return Lists.newArrayList();
        }
        NovBombGameDO game = novBombGameMapper.getLastUnFinishGame(userId);
        if (game == null) {
            return Lists.newArrayList();
        }

        List<NovBombGamePlayDO> rewardHistoryList = novBombGamePlayMapper.getRewardHistory(num, game.getId());
        return BeanCopyUtil.mapList(rewardHistoryList, NovBombRewardVO.class);
    }

}
