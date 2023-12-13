package com.csgo.service.roll;

import com.csgo.domain.plus.roll.RollGiftPlus;
import com.csgo.mapper.plus.roll.RollGiftPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Admin on 2021/6/25
 */
@Service
public class RollGiftService {
    @Autowired
    private RollGiftPlusMapper mapper;

    public List<RollGiftPlus> find(Integer rollId) {
        return mapper.find(rollId);
    }

    public List<RollGiftPlus> findByRollIds(Collection<Integer> rollIds) {
        if (CollectionUtils.isEmpty(rollIds)) {
            return new ArrayList<>();
        }
        return mapper.findByRollIds(rollIds);
    }
}
