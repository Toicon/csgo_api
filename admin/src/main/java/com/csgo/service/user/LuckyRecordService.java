package com.csgo.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserLuckyRecordCondition;
import com.csgo.domain.plus.user.LuckyRecord;
import com.csgo.domain.plus.user.UserLuckyRecordDTO;
import com.csgo.mapper.plus.user.LuckyRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author admin
 */
@Service
public class LuckyRecordService {

    @Autowired
    private LuckyRecordMapper luckyRecordMapper;

    @Transactional
    public void insert(LuckyRecord luckyRecord) {
        luckyRecordMapper.insert(luckyRecord);
    }

    public Page<UserLuckyRecordDTO> pagination(SearchUserLuckyRecordCondition condition) {
        return luckyRecordMapper.pagination(condition.getPage(), condition);
    }
}
