package com.csgo.service.lottery;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.Gift;
import com.csgo.domain.plus.user.UserMessageGiftPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.mapper.plus.user.UserMessageGiftPlusMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.modular.backpack.service.UserBackpackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeyBoxDrawService {

    private final UserMessagePlusMapper userMessagePlusMapper;
    private final UserBackpackService userBackpackService;

    private final UserMessageGiftPlusMapper userMessageGiftPlusMapper;

    /**
     * 使用钥匙
     */
    @Transactional(rollbackFor = Exception.class)
    public void useKey(int userId, Gift gift, int num) {
        if (num <= 0) {
            log.error("[开箱] 钥匙礼包配置错误 giftId:{} num:{}", gift.getId(), num);
            throw BizClientException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        if (gift.getKeyProductId() == null) {
            log.error("[开箱] 钥匙礼包配置错误 giftId:{}", gift.getId());
            throw BizClientException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        Integer perKeyNum = gift.getKeyNum();
        if (perKeyNum == null || perKeyNum <= 0) {
            log.error("[开箱] 钥匙礼包配置错误 giftId:{} perKeyNum:{}", gift.getId(), perKeyNum);
            throw BizClientException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        int useKeyNum = perKeyNum * num;
        List<UserMessagePlus> keyList = userMessagePlusMapper.listUnUseGiftKeyProduct(userId, gift.getKeyProductId(), useKeyNum);
        if (keyList.size() < useKeyNum) {
            throw BizClientException.of(CommonBizCode.KEY_BOX_KEY_LEAK);
        }
        for (UserMessagePlus message : keyList) {
            message.setState("1");
            userMessagePlusMapper.updateById(message);
        }

        List<Integer> messageIdList = keyList.stream().map(UserMessagePlus::getId).collect(Collectors.toList());
        List<UserMessageGiftPlus> giftMessageList = userMessageGiftPlusMapper.findByMessageIds(messageIdList);
        for (UserMessageGiftPlus messageGift : giftMessageList) {
            messageGift.setState(1);
            messageGift.setSellMoney(BigDecimal.ZERO);
            userMessageGiftPlusMapper.updateById(messageGift);
        }

        //背包流水详情记录
        userBackpackService.batchOutPackage(userId, "使用碎片", keyList);
    }
}
