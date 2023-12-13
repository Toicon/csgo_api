package com.csgo.service.roll;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.roll.SearchRollCoinsCondition;
import com.csgo.domain.plus.roll.RollCoins;
import com.csgo.mapper.plus.roll.RollCoinsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Service
public class RollCoinsService {

    @Autowired
    private RollCoinsMapper rollCoinsMapper;

    @Transactional
    public void insert(List<RollCoins> rollCoinsList) {
        rollCoinsList.forEach(coin -> {
            coin.setCt(new Date());
            rollCoinsMapper.insert(coin);
        });
    }

    public List<RollCoins> findByRollIds(Collection<Integer> rollIds) {
        if (CollectionUtils.isEmpty(rollIds)) {
            return new ArrayList<>();
        }
        return rollCoinsMapper.findByRollIds(rollIds);
    }

    public Page<RollCoins> pagination(SearchRollCoinsCondition condition) {
        return rollCoinsMapper.pagination(condition);
    }

    @Transactional
    public void delete(int id) {
        rollCoinsMapper.deleteById(id);
    }

    @Transactional
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            rollCoinsMapper.deleteById(id);
        }
    }

    public RollCoins get(int id) {
        return rollCoinsMapper.selectById(id);
    }

    @Transactional
    public void update(RollCoins rollCoins) {
        rollCoinsMapper.updateById(rollCoins);
    }
}
