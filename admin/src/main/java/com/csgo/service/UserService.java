package com.csgo.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserPlusCondition;
import com.csgo.constants.UserConstants;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.SysDept;
import com.csgo.domain.plus.anchor.AdminUserAnchorPlus;
import com.csgo.domain.plus.user.*;
import com.csgo.domain.report.StatisticsDTO;
import com.csgo.domain.user.User;
import com.csgo.mapper.UserMapper;
import com.csgo.mapper.plus.anchor.AdminUserAnchorPlusMapper;
import com.csgo.mapper.plus.user.UserInnerRechargeLimitMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.user.enums.UserStatusEnums;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.dept.SysDeptService;
import com.csgo.service.membership.MembershipService;
import com.csgo.service.user.UserSteamUpdateRecordService;
import com.csgo.support.GlobalConstants;
import com.csgo.util.DateUtilsEx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class UserService {
    private static final String CACHE_NAME = "user-frozen-";
    private static final BigDecimal REWARD_MONEY = new BigDecimal("0.5");
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private UserSteamUpdateRecordService userSteamUpdateRecordService;
    @Autowired
    private MembershipService membershipService;
    @Autowired
    private UserInnerRechargeLimitMapper userInnerRechargeLimitMapper;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminUserAnchorPlusMapper adminUserAnchorPlusMapper;

    public List<UserPlus> findByRoomNum(String roomNum) {
        return userPlusMapper.findByRoomNum(roomNum);
    }

    @Autowired
    private SysDeptService deptService;

    @Autowired
    private UserBalanceService userBalanceService;

    
    @Transactional
    public void insert(UserPlus user, Integer adminUserId) {
        user.setCreatedAt(new Date());
        if (GlobalConstants.INTERNAL_USER_FLAG == user.getFlag()) {
            //测试账号新增实名认证奖励
            if (user.getBalance() == null) {
                user.setBalance(REWARD_MONEY);
            } else {
                user.setBalance(user.getBalance().add(REWARD_MONEY));
            }
            userPlusMapper.insert(user);
            userBalanceService.addUserBalance(user, REWARD_MONEY, "实名认证奖励");
        } else {
            userPlusMapper.insert(user);
        }
        membershipService.save(user.getId());
        //保存后台管理账号关联
        this.addAdminUserRelationAnchor(user.getId(), adminUserId);
    }

    @Transactional
    public void update(User user, int id) {
        user.setId(id);
        userMapper.updateByPrimaryKeySelective(user);
    }

    public User queryUserId(int id) {
        User user = new User();
        user.setId(id);
        return userMapper.selectOne(user);
    }

    public List<User> selectList(User user) {
        return userMapper.selectList(user);
    }

    public Page<UserPlusDTO> pagination(SearchUserPlusCondition condition) {
        if (UserStatusEnums.FROZEN.getCode().equals(condition.getStatus())) {
            condition.setFrozen(true);
            condition.setStatus(null);
        } else if (UserStatusEnums.NORMAL.getCode().equals(condition.getStatus())) {
            condition.setFrozen(false);
        }
        return userPlusMapper.pagination(condition.getPage(), condition);
    }

    public int delete(int id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    public User getUserByExtensionUrl(String extensionUrl) {
        return userMapper.getUserByExtensionUrl(extensionUrl);
    }

    public User getUserByExtensionCode(String extensionCode) {
        return userMapper.getUserByExtensionCode(extensionCode);
    }

    public User getUserByUserName(String userName) {
        return userMapper.getUserByUserName(userName);
    }

    public User getUserById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public List<UserPlus> findByUserIds(List<Integer> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return userPlusMapper.findByIds(userIds);
    }

    public List<UserPlus> findAllInsideUser(String userName) {
        return userPlusMapper.findAllInsideUser(userName);
    }

    @Transactional
    public UserPlus getUserPlus(int userId) {
        return userPlusMapper.selectById(userId);
    }

    @Transactional
    public void frozen(UserPlus userPlus) {
        update(userPlus);
        redisTemplateFacde.delete(CACHE_NAME + userPlus.getId());
    }

    @Transactional
    public void update(UserPlus userPlus) {
        userPlusMapper.updateById(userPlus);
    }

    @Transactional
    public void update(UserPlus userPlus, String before, String name, Integer adminUserId) {
        if (StringUtils.hasText(before)) {
            userSteamUpdateRecordService.add(userPlus.getId(), before, userPlus.getSteam(), SteamUpdateType.ADMIN, name);
        }
        userPlusMapper.updateById(userPlus);
        this.updateAdminUserRelationAnchor(userPlus.getId(), adminUserId);
    }

    public List<StatisticsDTO> countAddUser(String startDate, String endDate) {
        String dataScope = adminUserService.getUserDataScope("adminUser");
        return userPlusMapper.countAddUser(startDate, endDate, dataScope);
    }

    public Integer countOldUser(Integer deptId, String startDate) {
        //未归属部门id
        Integer count = 0;
        count = count + userPlusMapper.countOldUser(deptId, startDate);
        if (deptId.equals(UserConstants.NO_DEPART_ID)) {
            count = count + userPlusMapper.countOldUserNotOwner(startDate);
        }
        return count;
    }

    public int countUserNum(Date startDate, Date endDate) {
        return userPlusMapper.countUserNum(startDate, endDate);
    }

    public boolean validateInnerRecharge(int userId) {
        UserInnerRechargeLimit recharge = userInnerRechargeLimitMapper.getNotRemoveByUserId(userId);
        return null != recharge;
    }

    public boolean validateWhiteInnerRecharge(int userId) {
        UserInnerRechargeLimit recharge = userInnerRechargeLimitMapper.getNotRemoveWhiteByUserId(userId);
        return null != recharge;
    }

    @Transactional
    public void delRechargeLimit(int userId, String source) {
        UserInnerRechargeLimit recharge = userInnerRechargeLimitMapper.getNotRemoveByUserId(userId);
        if (null == recharge) {
            return;
        }
        recharge.setOvertime(true);
        BaseEntity.updated(recharge, source);
        userInnerRechargeLimitMapper.updateById(recharge);
    }

    @Transactional
    public void delRechargeLimitWhite(int userId, String source) {
        UserInnerRechargeLimit recharge = userInnerRechargeLimitMapper.getNotRemoveWhiteByUserId(userId);
        if (null == recharge) {
            return;
        }
        recharge.setOvertime(true);
        BaseEntity.updated(recharge, source);
        userInnerRechargeLimitMapper.updateById(recharge);
    }

    @Transactional
    public void addRechargeLimit(int userId, String source) {
        UserInnerRechargeLimit recharge = userInnerRechargeLimitMapper.getNotRemoveByUserId(userId);
        if (null != recharge) {
            return;
        }
        UserInnerRechargeLimit rechargeLimit = new UserInnerRechargeLimit();
        rechargeLimit.setEndTime(DateUtilsEx.calDateByMinute(new Date(), 10));
        rechargeLimit.setOvertime(false);
        rechargeLimit.setWhite(false);
        rechargeLimit.setUserId(userId);
        BaseEntity.created(rechargeLimit, source);
        userInnerRechargeLimitMapper.insert(rechargeLimit);
    }

    @Transactional
    public void addWhiteRechargeLimit(int userId, String source) {
        UserInnerRechargeLimit recharge = userInnerRechargeLimitMapper.getNotRemoveWhiteByUserId(userId);
        if (null != recharge) {
            return;
        }
        UserInnerRechargeLimit rechargeLimit = new UserInnerRechargeLimit();
        rechargeLimit.setEndTime(DateUtilsEx.calDateByMinute(new Date(), 10));
        rechargeLimit.setOvertime(false);
        rechargeLimit.setWhite(true);
        rechargeLimit.setUserId(userId);
        BaseEntity.created(rechargeLimit, source);
        userInnerRechargeLimitMapper.insert(rechargeLimit);
    }

    public List<UserInnerRechargeLimit> findWhiteList() {
        return userInnerRechargeLimitMapper.findWhiteList();
    }


    /**
     * 新增主播用户关系
     *
     * @param userId
     * @param adminUserId
     */
    private void addAdminUserRelationAnchor(Integer userId, Integer adminUserId) {
        if (userId == null || adminUserId == null) {
            return;
        }
        AdminUserAnchorPlus adminUserAnchorPlus = new AdminUserAnchorPlus();
        adminUserAnchorPlus.setUserId(userId);
        adminUserAnchorPlus.setAdminUserId(adminUserId);
        adminUserAnchorPlus.setCt(new Date());
        adminUserAnchorPlusMapper.insert(adminUserAnchorPlus);
    }


    /**
     * 修改主播用户关系
     *
     * @param userId
     * @param adminUserId
     */
    private void updateAdminUserRelationAnchor(Integer userId, Integer adminUserId) {
        if (userId == null || adminUserId == null) {
            return;
        }
        LambdaQueryWrapper<AdminUserAnchorPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserAnchorPlus::getUserId, userId);
        AdminUserAnchorPlus adminUserAnchorPlus = adminUserAnchorPlusMapper.selectOne(wrapper);
        if (adminUserAnchorPlus != null) {
            adminUserAnchorPlus.setAdminUserId(adminUserId);
            adminUserAnchorPlus.setUt(new Date());
            adminUserAnchorPlusMapper.updateById(adminUserAnchorPlus);
        } else {
            adminUserAnchorPlus = new AdminUserAnchorPlus();
            adminUserAnchorPlus.setUserId(userId);
            adminUserAnchorPlus.setAdminUserId(adminUserId);
            adminUserAnchorPlus.setCt(new Date());
            adminUserAnchorPlusMapper.insert(adminUserAnchorPlus);
        }
    }

    /**
     * 批量用户id获取adminUserId
     *
     * @param userIds
     */
    public Map getAdminUserIdByUserId(List<Integer> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return null;
        }
        Map<Integer, Integer> map = new HashMap<>();
        LambdaQueryWrapper<AdminUserAnchorPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AdminUserAnchorPlus::getUserId, userIds);
        List<AdminUserAnchorPlus> adminUserAnchorPlusList = adminUserAnchorPlusMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(adminUserAnchorPlusList)) {
            return null;
        } else {
            for (AdminUserAnchorPlus adminUserAnchorPlus : adminUserAnchorPlusList) {
                map.put(adminUserAnchorPlus.getUserId(), adminUserAnchorPlus.getAdminUserId());
            }
        }
        return map;
    }

    /**
     * 校验用户昵称是否重复
     *
     * @param userId
     * @param name
     */
    public boolean checkNameExits(Integer userId, String name) {
        int count = userPlusMapper.getNameCount(userId, name);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断用户选择部门是否还有子部门
     *
     * @param adminUserId
     */
    public boolean checkDepartHasChild(Integer adminUserId) {
        if (adminUserId == null) {
            return false;
        }
        AdminUserPlus adminUserPlus = adminUserService.get(adminUserId);
        if (adminUserPlus == null || adminUserPlus.getDeptId() == null) {
            return false;
        }
        Integer deptId = adminUserPlus.getDeptId();
        SysDept dept = new SysDept();
        List<SysDept> deptList = deptService.dataScopeList(dept);
        SysDept chooseDept = new SysDept();
        chooseDept.setId(deptId);
        return deptService.hasChild(deptList, chooseDept);
    }
}
