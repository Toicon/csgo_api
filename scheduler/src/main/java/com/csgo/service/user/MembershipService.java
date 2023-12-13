package com.csgo.service.user;

import com.csgo.domain.plus.membership.Membership;
import com.csgo.domain.plus.membership.MembershipLevelConfig;
import com.csgo.domain.plus.membership.MembershipLevelRecord;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.mapper.plus.membership.MembershipLevelConfigMapper;
import com.csgo.mapper.plus.membership.MembershipLevelRecordMapper;
import com.csgo.mapper.plus.membership.MembershipMapper;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import com.csgo.support.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2021/12/14
 */
@Service
public class MembershipService {
    @Autowired
    private MembershipMapper membershipMapper;
    @Autowired
    private OrderRecordMapper orderRecordMapper;
    @Autowired
    private MembershipLevelConfigMapper membershipLevelConfigMapper;
    @Autowired
    private MembershipLevelRecordMapper membershipLevelRecordMapper;

    public void process() {
        Date date = new Date();
        List<Membership> memberships = membershipMapper.findByDate(date);
        if (CollectionUtils.isEmpty(memberships)) {
            return;
        }
        memberships.forEach(this::updateGrade);
    }

    @Transactional
    public void updateGrade(Membership membership) {
        List<OrderRecord> orderRecords = orderRecordMapper.findLimitDate(membership.getUserId(), membership.getLastGradeDate(), new Date(), GlobalConstants.ORDER_STATUS_SUCCESS);
        BigDecimal totalAmount = orderRecords.stream().map(OrderRecord::getOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        MembershipLevelConfig config = membershipLevelConfigMapper.findByLevel(membership.getGrade());
        if (config == null) {
            return;
        }
        BigDecimal minLimit = config.getLevelLimit().multiply(BigDecimal.valueOf(0.08));
        if (totalAmount.compareTo(minLimit) < 0) {
            BigDecimal subGrowth = config.getLevelLimit().multiply(BigDecimal.valueOf(0.1)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal growth = membership.getGrowth().subtract(subGrowth);
            if (growth.compareTo(config.getLevelLimit()) < 0) {
                membership.setGrade(membership.getGrade() - 1);
                membership.setImg("");
            }
            membership.setGrowth(growth);
            record(membership, subGrowth, "衰减经验");
        }
        membership.setLastGradeDate(new Date());
        membershipMapper.updateById(membership);
    }

    private void record(Membership membership, BigDecimal growth, String remark) {
        MembershipLevelRecord record = new MembershipLevelRecord();
        record.setUserId(membership.getUserId());
        record.setGrowth(growth);
        record.setCurrentGrowth(membership.getGrowth());
        record.setCurrentGrade(membership.getGrade());
        record.setRemark(remark);
        record.setCt(new Date());
        membershipLevelRecordMapper.insert(record);
    }
}
