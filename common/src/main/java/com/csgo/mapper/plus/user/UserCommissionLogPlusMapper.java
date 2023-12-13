package com.csgo.mapper.plus.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserCommissionLogCondition;
import com.csgo.domain.plus.user.UserCommissionLogDTO;
import com.csgo.domain.plus.user.UserCommissionLogPlus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface UserCommissionLogPlusMapper extends BaseMapper<UserCommissionLogPlus> {

    BigDecimal getReceiveAmount(@Param("userId") Integer userId);

    default List<UserCommissionLogPlus> find(int status, Date settlementTime) {
        LambdaQueryWrapper<UserCommissionLogPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCommissionLogPlus::getStatus, status);
        wrapper.eq(UserCommissionLogPlus::getSettlementTime, settlementTime);
        return selectList(wrapper);
    }

    Page<UserCommissionLogDTO> pagination(IPage<UserCommissionLogDTO> page, @Param("condition") SearchUserCommissionLogCondition condition);

    BigDecimal recommendCommission(@Param("userId") Integer userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 主播下级散户挂散户，下下级散户充值金额归属到未归属主播
     *
     * @param startDate
     * @param endDate
     * @return
     */
    BigDecimal recommendCommissionOther(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
