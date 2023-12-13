package com.csgo.service.membership;

import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Service
public class MembershipTaskRecordService {
//    @Autowired
//    private MembershipTaskRecordMapper mapper;
//    @Autowired
//    private MembershipLevelConfigMapper membershipLevelConfigMapper;
//    @Autowired
//    private MembershipMapper membershipMapper;
//    @Autowired
//    private MembershipLevelRecordMapper membershipLevelRecordMapper;
//
//    public MembershipTaskRecord getMembershipTaskRecord(int recordId) {
//        return mapper.selectById(recordId);
//    }
//
//    public Map<MembershipTaskRule, MembershipTaskRecord> findTaskRecord(int userId) {
//        List<MembershipTaskRecord> records = mapper.findByUser(userId, MembershipTaskRule.getTodayRules(), true);
//        MembershipTaskRecord inviteRecord = mapper.find(userId, MembershipTaskRule.INVITE, RecordStatus.STANDARD);
//        if (null != inviteRecord) {
//            records.add(inviteRecord);
//        }
//        return records.stream().collect(Collectors.toMap(MembershipTaskRecord::getRuleType, Function.identity()));
//    }
//
//    @Transactional
//    public void addRecord(MembershipTaskRule ruleType, int userId, int count) {
//        MembershipTaskRecord record = mapper.findByUser(userId, ruleType, true);
//        if (null == record) {
//            record = new MembershipTaskRecord();
//            record.setRuleType(ruleType);
//            record.setRuleCount(0);
//            record.setUserId(userId);
//            record.setRecordStatus(MembershipTaskStatus.NO_STANDARD);
//        }
//        if (MembershipTaskStatus.NO_STANDARD != record.getRecordStatus()) {
//            return;
//        }
//        record.setRuleCount(record.getRuleCount() + count);
//        if (record.getRuleCount() >= ruleType.getRuleCount()) {
//            record.setRecordStatus(MembershipTaskStatus.STANDARD);
//        }
//        if (record.getId() == 0) {
//            BaseEntity.created(record, ruleType.getDescription());
//            mapper.insert(record);
//        } else {
//            BaseEntity.updated(record, ruleType.getDescription());
//            mapper.updateById(record);
//        }
//    }
//
//    @Transactional
//    public void addRechargeRecord(UserPlus user) {
//        if (null == user.getInvitedDate() || null == user.getParentId() || user.getInvitedDate().before(DateUtils.truncate(new Date(), Calendar.DATE))) {
//            return;
//        }
//        List<MembershipTaskRecord> records = mapper.findUserInvite(user.getParentId(), MembershipTaskRule.INVITE, user.getId());
//        if (CollectionUtils.isEmpty(records)) {
//            MembershipTaskRecord record = new MembershipTaskRecord();
//            record.setRuleType(MembershipTaskRule.INVITE);
//            record.setRuleCount(1);
//            record.setUserId(user.getParentId());
//            record.setInviteUserId(user.getId());
//            record.setRecordStatus(MembershipTaskStatus.STANDARD);
//            BaseEntity.created(record, MembershipTaskRule.INVITE.getDescription());
//            mapper.insert(record);
//        }
//    }
//
//    @Transactional
//    public void update(MembershipTaskRecord taskRecord) {
//        taskRecord.setRecordStatus(MembershipTaskStatus.RECEIVE);
//        BaseEntity.updated(taskRecord, BillType.RECEIVE.getDescription());
//        mapper.updateById(taskRecord);
//
//        membershipHandler(taskRecord.getRuleType().getReward(), taskRecord);
//    }
//
//    private void membershipHandler(BigDecimal amount, MembershipTaskRecord taskRecord) {
//        Membership membership = membershipMapper.get(taskRecord.getUserId());
//        membership.setGrowth(membership.getGrowth().add(amount));
//        List<MembershipLevelConfig> configs = membershipLevelConfigMapper.maxLevel(membership.getGrowth());
//        if (!CollectionUtils.isEmpty(configs)) {
//            membership.setGrade(configs.get(0).getLevel());
//        }
//        membership.setUt(new Date());
//        membershipMapper.updateById(membership);
//        MembershipLevelRecord record = new MembershipLevelRecord();
//        record.setUserId(membership.getUserId());
//        record.setGrowth(amount);
//        record.setTaskId(taskRecord.getId());
//        record.setCurrentGrowth(membership.getGrowth());
//        record.setCurrentGrade(membership.getGrade());
//        record.setRemark(taskRecord.getRuleType().getDescription());
//        record.setCt(new Date());
//        membershipLevelRecordMapper.insert(record);
//    }
}
