package com.csgo.modular.backpack.service;

import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.modular.backpack.logic.UserMessageItemRecordLogic;
import com.csgo.modular.backpack.logic.UserMessageRecordLogic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 用户背包
 *
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBackpackService {

    private final UserMessageRecordLogic userMessageRecordLogic;
    private final UserMessageItemRecordLogic userMessageItemRecordLogic;

    @Transactional(rollbackFor = Exception.class)
    public void inPackage(Integer userId, String source, UserMessagePlus message) {
        int recordId = userMessageRecordLogic.inPackage(userId, source);
        userMessageItemRecordLogic.add(recordId, message.getId(), message.getImg());
    }

    @Transactional(rollbackFor = Exception.class)
    public void outPackage(Integer userId, String source, UserMessagePlus message) {
        int recordId = userMessageRecordLogic.outPackage(userId, source);
        userMessageItemRecordLogic.add(recordId, message.getId(), message.getImg());
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchOutPackage(Integer userId, String source, List<UserMessagePlus> packageList) {
        batchPackage(userId, source, UserMessageRecordLogic.OUT, packageList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchInPackage(Integer userId, String source, List<UserMessagePlus> packageList) {
        batchPackage(userId, source, UserMessageRecordLogic.IN, packageList);
    }

    private void batchPackage(Integer userId, String source, String operation, List<UserMessagePlus> packageList) {
        if (CollectionUtils.isEmpty(packageList)) {
            log.warn("[批量操作背包] packageList为空 userId:{}", userId);
            return;
        }
        int recordId = userMessageRecordLogic.add(userId, source, operation);
        for (UserMessagePlus backpack : packageList) {
            userMessageItemRecordLogic.add(recordId, backpack.getId(), backpack.getImg());
        }
    }


}
