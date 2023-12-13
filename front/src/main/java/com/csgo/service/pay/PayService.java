package com.csgo.service.pay;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.exception.ServiceErrorException;
import com.csgo.framework.exception.BizClientException;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import com.csgo.mapper.plus.user.UserPrizePlusMapper;
import com.csgo.support.StandardExceptionCode;
import com.echo.framework.platform.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author admin
 */
@Service
public class PayService {

    @Autowired
    private UserPrizePlusMapper userPrizePlusMapper;
    @Autowired
    private OrderRecordMapper orderRecordMapper;

    //未充值的用户，只能开1次箱子。第二次必须充值。
    public void validateDraw(int userId) {
        int sum = userPrizePlusMapper.countByUserId(userId);
        if (sum == 0) {
            return;
        }
        List<OrderRecord> orderRecords = orderRecordMapper.findByUserIds(Collections.singletonList(userId));
        if (CollectionUtils.isEmpty(orderRecords)) {
            throw BizClientException.of(CommonBizCode.DRAW_LIMIT);
        }
    }

    //未充值的用户，只能开1次箱子。第二次必须充值。
    public void validateFishDraw(int userId) {
        int sum = userPrizePlusMapper.countByUserId(userId);
        if (sum == 0) {
            return;
        }
        List<OrderRecord> orderRecords = orderRecordMapper.findByUserIds(Collections.singletonList(userId));
        if (CollectionUtils.isEmpty(orderRecords)) {
            throw new ServiceErrorException("充值任意金额开通钓鱼权限");
        }
    }
}
