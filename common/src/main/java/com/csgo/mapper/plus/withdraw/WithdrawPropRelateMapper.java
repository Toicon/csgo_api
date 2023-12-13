package com.csgo.mapper.plus.withdraw;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.enums.WithdrawPropItemStatus;
import com.csgo.domain.plus.withdraw.WithdrawPropRelate;
import com.csgo.domain.plus.withdraw.WithdrawPropRelateDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface WithdrawPropRelateMapper extends BaseMapper<WithdrawPropRelate> {

    BigDecimal countSpendingTotal(Date startDate, Date endDate);

    default List<WithdrawPropRelate> findRelateByPropId(Integer propId) {
        LambdaQueryWrapper<WithdrawPropRelate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WithdrawPropRelate::getPopId, propId);
        return selectList(wrapper);
    }

    default List<WithdrawPropRelate> findByStatus(List<WithdrawPropItemStatus> statuses) {
        LambdaQueryWrapper<WithdrawPropRelate> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(WithdrawPropRelate::getStatus, statuses);
        return selectList(wrapper);
    }

    default List<WithdrawPropRelate> findByPropIdAndStatus(Integer propId) {
        LambdaQueryWrapper<WithdrawPropRelate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WithdrawPropRelate::getPopId, propId);
        wrapper.in(WithdrawPropRelate::getStatus, Arrays.asList(WithdrawPropItemStatus.PENDING, WithdrawPropItemStatus.AUTO));
        wrapper.isNull(WithdrawPropRelate::getSteamUrl);
        return selectList(wrapper);
    }

    List<WithdrawPropRelateDTO> findByStatusAndUserId(Integer userId);

    WithdrawPropRelateDTO findWithdrawAll(@Param("userName") String userName, @Param("flag") Integer flag, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    default List<WithdrawPropRelate> findByMessageIds(List<Integer> messageIds) {
        LambdaQueryWrapper<WithdrawPropRelate> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(WithdrawPropRelate::getMessageId, messageIds);
        return selectList(wrapper);
    }
}
