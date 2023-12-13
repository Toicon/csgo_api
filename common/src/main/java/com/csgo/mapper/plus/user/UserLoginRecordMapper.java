package com.csgo.mapper.plus.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserLoginRecordCondition;
import com.csgo.domain.plus.user.UserLoginRecord;
import com.csgo.domain.plus.user.UserLoginRecordDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface UserLoginRecordMapper extends BaseMapper<UserLoginRecord> {
    /**
     * 分页查询
     *
     * @param page
     * @param condition
     * @return
     */
    Page<UserLoginRecordDTO> pagination(IPage<UserLoginRecordDTO> page, @Param("condition") SearchUserLoginRecordCondition condition);
}
