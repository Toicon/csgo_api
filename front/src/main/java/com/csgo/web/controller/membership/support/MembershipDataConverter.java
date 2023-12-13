package com.csgo.web.controller.membership.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.domain.plus.membership.MembershipLevelConfig;
import com.csgo.domain.plus.membership.MembershipTaskRecord;
import com.csgo.domain.plus.membership.MembershipTaskRule;
import com.csgo.domain.plus.membership.MembershipTaskStatus;
import com.csgo.domain.plus.user.UserPrizePlus;
import com.csgo.service.gift.GiftService;
import com.csgo.service.membership.MembershipLevelConfigService;
import com.csgo.service.user.UserPrizeService;
import com.csgo.web.response.membership.MembershipTaskBoxResponse;
import com.csgo.web.response.membership.MembershipTaskInfoResponse;

/**
 * @author admin
 */
@Service
public class MembershipDataConverter {
    @Autowired
    private GiftService giftService;
    @Autowired
    private MembershipLevelConfigService membershipLevelConfigService;
    @Autowired
    private UserPrizeService userPrizeService;

    public List<MembershipTaskInfoResponse> to(Map<MembershipTaskRule, MembershipTaskRecord> map) {
        List<MembershipTaskInfoResponse> results = new ArrayList<>();

        for (MembershipTaskRule ruleType : MembershipTaskRule.values()) {
            MembershipTaskInfoResponse result = new MembershipTaskInfoResponse();
            result.setRuleType(ruleType);
            MembershipTaskRecord record = map.get(ruleType);
            if (null == record) {
                result.setId(0);
                result.setRecordStatus(MembershipTaskStatus.NO_STANDARD);
                result.setCurrentCount(0);
                result.setTotalCount(ruleType.getRuleCount());
                result.setReward(ruleType.getReward());
            } else {
                result.setId(record.getId());
                result.setReward(ruleType.getReward());
                result.setTotalCount(ruleType.getRuleCount());
                result.setRecordStatus(record.getRecordStatus());
                if (MembershipTaskStatus.NO_STANDARD.equals(record.getRecordStatus())) {
                    result.setCurrentCount(record.getRuleCount());
                } else {
                    result.setCurrentCount(ruleType.getRuleCount());
                }
            }
            results.add(result);
        }
        return results;
    }

    public List<MembershipTaskBoxResponse> to(Integer grade, Integer userId) {
        List<GiftPlus> gifts;
        List<MembershipLevelConfig> configs;
        gifts = giftService.findAllMembership();
        configs = membershipLevelConfigService.list();
        gifts = gifts.stream().filter(gift -> {
            UserPrizePlus userPrizePlus = userPrizeService.findByUserIdAndGiftId(userId, gift.getId());
            return null == userPrizePlus;
        }).collect(Collectors.toList());
        Map<Integer, MembershipLevelConfig> configMap = configs.stream().collect(Collectors.toMap(MembershipLevelConfig::getLevel, config -> config));
        return gifts.stream().map(giftPlus -> {
            MembershipTaskBoxResponse response = new MembershipTaskBoxResponse();
            response.setGiftId(giftPlus.getId());
            response.setGiftImg(giftPlus.getImg());
            if (configMap.containsKey(giftPlus.getMembershipGrade())) {
                response.setGiftLevelLimit(configMap.get(giftPlus.getMembershipGrade()).getLevelLimit());
            }
            response.setCanOpen(giftPlus.getMembershipGrade() <= grade);
            return response;
        }).collect(Collectors.toList());
    }
}
