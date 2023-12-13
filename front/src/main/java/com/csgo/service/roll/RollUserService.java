package com.csgo.service.roll;

import com.csgo.domain.RollUser;
import com.csgo.domain.plus.roll.RollUserPlus;
import com.csgo.mapper.RollUserMapper;
import com.csgo.mapper.plus.roll.RollUserPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2021/5/21
 */
@Service
public class RollUserService {
    @Autowired
    private RollUserPlusMapper mapper;
    @Autowired
    private RollUserMapper rollUserMapper;

    public List<RollUserPlus> find(Integer rollId, String flag) {
        return mapper.find(null, rollId, flag, null);
    }

    @Transactional
    public void update(RollUserPlus rollUser) {
        rollUser.setUt(new Date());
        mapper.updateById(rollUser);
    }

    @Transactional
    public void add(RollUser rollUser) {
        rollUserMapper.insert(rollUser);
    }

    public List<RollUserPlus> find(int userId, int rollId) {
        return mapper.find(userId, rollId, null, null);
    }

    public List<RollUserPlus> findByUserId(Integer userId) {
        return mapper.find(userId, null, null, null);
    }

    public List<RollUserPlus> findByRollIds(List<Integer> rollIds) {
        return mapper.findByRollIds(rollIds);
    }
}
