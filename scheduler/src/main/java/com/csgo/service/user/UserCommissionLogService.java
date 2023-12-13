package com.csgo.service.user;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.csgo.domain.plus.user.UserCommissionLogPlus;
import com.csgo.mapper.plus.user.UserCommissionLogPlusMapper;
import com.csgo.support.GlobalConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserCommissionLogService {

    @Autowired
    private UserCommissionLogPlusMapper commissionLogPlusMapper;

    @Transactional
    public void refreshStatus() {
        Date current = new Date();
        Date settlementTime = DateUtils.truncate(current, Calendar.DATE);
        List<UserCommissionLogPlus> userCommissionLogs = commissionLogPlusMapper.find(GlobalConstants.USER_COMMISSION_LOG_DISABLED, settlementTime);
        log.info("update commission id: {}", userCommissionLogs.stream().map(UserCommissionLogPlus::getId).collect(Collectors.toList()));
        userCommissionLogs.forEach(userCommissionLogPlus -> {
            userCommissionLogPlus.setStatus(GlobalConstants.USER_COMMISSION_LOG_NOT_RECEIVE);
            userCommissionLogPlus.setUpdateTime(current);
            commissionLogPlusMapper.updateById(userCommissionLogPlus);
        });
    }
}
