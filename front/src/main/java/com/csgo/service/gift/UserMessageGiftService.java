package com.csgo.service.gift;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.SearchUserMessageGiftCondition;
import com.csgo.domain.plus.user.UserMessageGiftPlus;
import com.csgo.domain.plus.withdraw.WithdrawPropRelate;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.framework.mybatis.util.MyBatisUtils;
import com.csgo.mapper.UserMessageGiftMapper;
import com.csgo.mapper.plus.user.UserMessageGiftPlusMapper;
import com.csgo.mapper.plus.withdraw.WithdrawPropRelateMapper;
import com.csgo.framework.mo.RecentDateMO;
import com.csgo.web.request.gift.SearchUserMessageGiftRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Service
public class UserMessageGiftService {

    @Autowired
    private UserMessageGiftMapper messageMapper;
    @Autowired
    private UserMessageGiftPlusMapper userMessageGiftPlusMapper;
    @Autowired
    private WithdrawPropRelateMapper relateMapper;

    @Transactional
    public void insert(UserMessageGift userMessageGift) {
        messageMapper.insert(userMessageGift);
    }

    public Page<UserMessageGiftPlus> getFrontPage(Integer dateRangeType, Integer userId, SearchUserMessageGiftRequest request) {
        RecentDateMO.Period period = RecentDateMO.getPeriod(dateRangeType);

        LambdaQueryWrapper<UserMessageGiftPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserMessageGiftPlus::getUserId, userId);
        if (period != null) {
            wrapper.ge(UserMessageGiftPlus::getCt, period.getStart().toDate());
        }
        wrapper.orderByDesc(UserMessageGiftPlus::getId);

        Page<UserMessageGiftPlus> pageable = MyBatisUtils.buildPage(request);
        return userMessageGiftPlusMapper.selectPage(pageable, wrapper);
    }

    public UserMessageGiftPlus getByMessageId(Integer userMessageId) {
        return userMessageGiftPlusMapper.findByMessageId(userMessageId);
    }

    @Transactional
    public void updatePlus(UserMessageGiftPlus userMessageGift) {
        userMessageGiftPlusMapper.updateById(userMessageGift);
    }

    public List<UserMessageGiftPlus> findByGiftProductIds(Collection<Integer> giftProductIds, String excludeGiftType) {
        return userMessageGiftPlusMapper.findRecent(giftProductIds, excludeGiftType);
    }

    public Page<UserMessageGiftPlus> extractPage(SearchUserMessageGiftCondition condition) {
        Page<UserMessageGiftPlus> page = userMessageGiftPlusMapper.extractPage(condition);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<Integer> messageIds = page.getRecords().stream().map(UserMessageGiftPlus::getId).collect(Collectors.toList());
            List<WithdrawPropRelate> withdrawPropRelateList = relateMapper.findByMessageIds(messageIds);
            if (CollectionUtils.isNotEmpty(withdrawPropRelateList)) {
                for (UserMessageGiftPlus userMessageGiftPlus : page.getRecords()) {
                    WithdrawPropRelate withdrawPropRelate = withdrawPropRelateList.stream().filter(item -> item.getMessageId() == userMessageGiftPlus.getId().intValue()).findFirst().orElse(null);
                    if (withdrawPropRelate != null) {
                        userMessageGiftPlus.setUt(withdrawPropRelate.getUt());
                    }
                }
                page.setRecords(page.getRecords().stream().sorted(Comparator.comparing(UserMessageGiftPlus::getUt, Comparator.reverseOrder())).collect(Collectors.toList()));
            }
        }
        return page;
    }
}
