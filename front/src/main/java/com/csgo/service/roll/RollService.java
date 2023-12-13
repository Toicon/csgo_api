package com.csgo.service.roll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.csgo.domain.plus.roll.RollPlus;
import com.csgo.mapper.plus.roll.RollPlusMapper;

/**
 * @author admin
 */
@Service
public class RollService {

    @Autowired
    private RollPlusMapper rollPlusMapper;

    public RollPlus get(int id) {
        return rollPlusMapper.selectById(id);
    }

    public List<RollPlus> find(String status) {
        return rollPlusMapper.find(status);
    }

    public List<RollPlus> findTop3() {
        return rollPlusMapper.findTop3();
    }

    public List<RollPlus> find(Collection<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return rollPlusMapper.find(ids);
    }

    public List<RollPlus> findTopN(Integer num) {
        return rollPlusMapper.findTopN(num);
    }
}
