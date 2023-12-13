package com.csgo.mapper.plus.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserLuckyRecordCondition;
import com.csgo.domain.plus.user.LuckyRecord;
import com.csgo.domain.plus.user.UserLuckyRecordDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface LuckyRecordMapper extends BaseMapper<LuckyRecord> {

    Page<UserLuckyRecordDTO> pagination(IPage<UserLuckyRecordDTO> page, @Param("condition") SearchUserLuckyRecordCondition condition);
}
