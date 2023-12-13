package com.csgo.modular.pay.hook;

import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.user.UserPlus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRechargeHookManage {

    @Autowired(required = false)
    private List<UserRechargeHook> userRechargeHookList;

    public void handleUserRechargeSuccess(UserPlus user, OrderRecord orderRecord) {
        if (userRechargeHookList == null) {
            return;
        }
        if (user == null) {
            log.error("[用户充值][Hook] user为空");
            return;
        }
        if (orderRecord == null) {
            log.error("[用户充值][Hook] order为空");
            return;
        }

        for (UserRechargeHook hook : userRechargeHookList) {
            try {
                hook.handleRechargeSuccess(user, orderRecord);
            } catch (Exception e) {
                log.error("[用户充值][Hook]", e);
            }
        }
    }

}
