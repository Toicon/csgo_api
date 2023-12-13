package com.csgo.service.roll;

import com.csgo.domain.plus.roll.RollCoins;
import com.csgo.mapper.plus.roll.RollCoinsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author admin
 */
@Service
public class RollCoinsService {

    @Autowired
    private RollCoinsMapper rollCoinsMapper;

    public List<RollCoins> find(int rollId) {
        return rollCoinsMapper.find(rollId);
    }

    @Transactional
    public void delete(int id) {
        rollCoinsMapper.deleteById(id);
    }

    public RollCoins get(int id) {
        return rollCoinsMapper.selectById(id);
    }

    @Transactional
    public void update(RollCoins rollCoins) {
        rollCoinsMapper.updateById(rollCoins);
    }

    public List<RollCoins> findByRollIds(Collection<Integer> rollIds) {
        if (CollectionUtils.isEmpty(rollIds)) {
            return new ArrayList<>();
        }
        return rollCoinsMapper.findByRollIds(rollIds);
    }
}
