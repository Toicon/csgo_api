package com.csgo.service.membership;

import com.csgo.domain.plus.membership.MembershipLevelConfig;
import com.csgo.mapper.plus.membership.MembershipLevelConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Admin on 2021/12/14
 */
@Service
public class MembershipLevelConfigService {
    @Autowired
    private MembershipLevelConfigMapper mapper;

    public List<MembershipLevelConfig> list() {
        return mapper.list();
    }

    public MembershipLevelConfig nextLevel(Integer level) {
        return mapper.nextLevel(level);
    }

    public List<MembershipLevelConfig> findLeLevel(Integer level) {
        return mapper.findLeLevel(level);
    }
}
