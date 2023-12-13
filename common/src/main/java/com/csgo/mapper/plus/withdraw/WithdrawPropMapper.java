package com.csgo.mapper.plus.withdraw;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.withdraw.SearchWithdrawPropCondition;
import com.csgo.domain.plus.withdraw.WithdrawProp;
import com.csgo.domain.plus.withdraw.WithdrawPropDTO;
import com.csgo.domain.plus.withdraw.WithdrawPropPriceDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface WithdrawPropMapper extends BaseMapper<WithdrawProp> {

    List<WithdrawPropPriceDTO> findByUserIds(@Param("userIds") List<Integer> userIds);

    Page<WithdrawPropDTO> pagination(IPage<WithdrawPropDTO> page, @Param("condition") SearchWithdrawPropCondition condition);

    /**
     * 10分钟内自动提取有效次数
     *
     * @param userId
     * @param drawDate
     * @return
     */
    int withdrawCount(@Param("userId") Integer userId, @Param("drawDate") Date drawDate);
}
