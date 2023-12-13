package com.csgo.service.prize;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.prize.SearchUserPrizeDTOCondition;
import com.csgo.domain.plus.user.UserPrizeDTO;
import com.csgo.mapper.plus.user.UserPrizePlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Service
public class UserPrizePlusService {

    @Autowired
    private UserPrizePlusMapper userPrizePlusMapper;

    public Page<UserPrizeDTO> pagination(SearchUserPrizeDTOCondition condition) {
        return userPrizePlusMapper.pagination(condition.getPage(), condition);
    }
}
