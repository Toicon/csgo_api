package com.csgo.service.roll;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.roll.SearchRollGiftPlusCondition;
import com.csgo.domain.plus.roll.RollGiftPlus;
import com.csgo.mapper.plus.roll.RollGiftPlusMapper;
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
public class RollGiftService {

    @Autowired
    private RollGiftPlusMapper mapper;

    @Transactional
    public void insert(RollGiftPlus giftPlus) {
        giftPlus.setCt(new Date());
        mapper.insert(giftPlus);
    }

    public List<RollGiftPlus> findByRollIds(Collection<Integer> rollIds) {
        if (CollectionUtils.isEmpty(rollIds)) {
            return new ArrayList<>();
        }
        return mapper.findByRollIds(rollIds);
    }

    public Page<RollGiftPlus> pagination(SearchRollGiftPlusCondition condition) {
        return mapper.pagination(condition);
    }

    @Transactional
    public void delete(int id) {
        mapper.deleteById(id);
    }

    public RollGiftPlus get(int id) {
        return mapper.selectById(id);
    }

    @Transactional
    public void updateUser(int id, int userId) {
        RollGiftPlus rollGiftPlus = mapper.selectById(id);
        rollGiftPlus.setUt(new Date());
        mapper.updateById(rollGiftPlus);
    }

    public List<RollGiftPlus> findByRollId(Integer rollId) {
        return mapper.find(rollId);
    }
}
