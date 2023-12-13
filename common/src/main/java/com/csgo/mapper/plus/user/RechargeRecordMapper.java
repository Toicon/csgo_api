package com.csgo.mapper.plus.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserRechargeRecordCondition;
import com.csgo.domain.plus.user.RechargeRecord;
import com.csgo.domain.plus.user.RechargeRecordDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface RechargeRecordMapper extends BaseMapper<RechargeRecord> {

    Page<RechargeRecordDTO> pagination(IPage<RechargeRecordDTO> page, @Param("condition") SearchUserRechargeRecordCondition condition);

}
