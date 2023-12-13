package com.csgo.service.roll;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.roll.SearchRollCondition;
import com.csgo.domain.plus.roll.RollPlus;
import com.csgo.domain.plus.roll.RollUserCountDTO;
import com.csgo.mapper.RollUserMapper;
import com.csgo.mapper.plus.roll.RollPlusMapper;
import com.csgo.support.GlobalConstants;
import com.echo.framework.platform.exception.ApiException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Service
public class RollService {

    @Autowired
    private RollPlusMapper rollPlusMapper;
    @Autowired
    private RollUserMapper rollUserMapper;

    @Transactional
    public void insert(RollPlus roll) {
        if (null != roll.getMinGrade()) {
            RollPlus exists = rollPlusMapper.findByGrade(roll.getMinGrade(), GlobalConstants.ROLL_STATUS_ACTIVE);
            if (exists != null) {
                throw new ApiException(HttpStatus.SC_BAD_REQUEST, "当前VIPRoll房已配置");
            }
        }
        roll.setCt(new Date());
        rollPlusMapper.insert(roll);
    }

    public List<RollPlus> findActive() {
        return rollPlusMapper.find(GlobalConstants.ROLL_STATUS_ACTIVE);
    }

    public Page<RollPlus> search(SearchRollCondition condition) {
        return rollPlusMapper.search(condition.getPage());
    }

    public RollPlus get(int id) {
        return rollPlusMapper.selectById(id);
    }

    @Transactional
    public void update(RollPlus rollPlus) {
        if (null != rollPlus.getMinGrade()) {
            RollPlus exists = rollPlusMapper.findByGrade(rollPlus.getMinGrade(), GlobalConstants.ROLL_STATUS_ACTIVE);
            if (exists != null && !rollPlus.getId().equals(exists.getId())) {
                throw new ApiException(HttpStatus.SC_BAD_REQUEST, "当前VIPRoll房已配置");
            }
        }
        rollPlus.setUt(new Date());
        rollPlusMapper.updateById(rollPlus);
    }

    @Transactional
    public void updateSwitch(int id) {
        RollPlus roll = rollPlusMapper.selectById(id);
        roll.setRoomSwitch(!roll.getRoomSwitch());
        rollPlusMapper.updateById(roll);
    }

    @Transactional
    public void delete(int id) {
        rollPlusMapper.deleteById(id);
    }

    public Map<Integer, Integer> getRollUserMap(Collection<Integer> rollIds) {
        List<RollUserCountDTO> rollUserCountDTOS = rollUserMapper.countRollUser(rollIds);
        if (CollectionUtils.isEmpty(rollUserCountDTOS)) {
            return Collections.emptyMap();
        }
        return rollUserCountDTOS.stream().collect(Collectors.toMap(RollUserCountDTO::getRollId, RollUserCountDTO::getUserCount));
    }
}
