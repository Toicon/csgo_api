package com.csgo.service.roll;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.csgo.domain.plus.roll.RollPlus;
import com.csgo.mapper.RollUserMapper;
import com.csgo.mapper.plus.roll.RollPlusMapper;
import com.csgo.support.GlobalConstants;

/**
 * @author admin
 */
@Service
public class RollService {

    @Autowired
    private RollPlusMapper rollPlusMapper;
    @Autowired
    private RollUserMapper rollUserMapper;

    public List<RollPlus> findActive() {
        return rollPlusMapper.find(GlobalConstants.ROLL_STATUS_ACTIVE);
    }

    @Transactional
    public void update(RollPlus rollPlus) {
        rollPlus.setUt(new Date());
        rollPlusMapper.updateById(rollPlus);
    }
}
