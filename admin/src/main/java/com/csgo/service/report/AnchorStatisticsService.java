package com.csgo.service.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.report.SearchAnchorStatisticsCondition;
import com.csgo.constants.UserConstants;
import com.csgo.domain.plus.anchor.AdminUserAnchorPlus;
import com.csgo.domain.report.AnchorStatisticsDTO;
import com.csgo.domain.report.AnchorUserDTO;
import com.csgo.domain.report.AnchorUserDeptDTO;
import com.csgo.domain.user.User;
import com.csgo.exception.AdminErrorException;
import com.csgo.mapper.UserMapper;
import com.csgo.mapper.plus.anchor.AdminUserAnchorPlusMapper;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import com.csgo.mapper.plus.user.UserCommissionLogPlusMapper;
import com.csgo.service.AdminUserService;
import com.csgo.support.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class AnchorStatisticsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderRecordMapper orderRecordMapper;
    @Autowired
    private UserCommissionLogPlusMapper userCommissionLogMapper;

    @Autowired
    private AdminUserAnchorPlusMapper adminUserAnchorPlusMapper;

    @Autowired
    private AdminUserService adminUserService;

    public Page<AnchorStatisticsDTO> pagination(SearchAnchorStatisticsCondition condition) {
        condition.setEndDate(condition.getEndDate() + GlobalConstants.END_TIME);
        //获取用户数据权限
        condition.setDataScope(adminUserService.getUserDataScope("adminUser"));
        Page<AnchorStatisticsDTO> page = userMapper.statisticsAnchor(condition.getPage(), condition);
        page.getRecords().forEach(anchorStatisticsDTO -> addItem(anchorStatisticsDTO, condition.getStartDate(), condition.getEndDate()));
        return page;
    }

    public Page<AnchorStatisticsDTO> financePagination(SearchAnchorStatisticsCondition condition) {
        condition.setEndDate(condition.getEndDate() + GlobalConstants.END_TIME);
        //获取用户数据权限
        condition.setDataScope(adminUserService.getUserDataScope("adminUser"));
        Page<AnchorStatisticsDTO> page = userMapper.statisticsAnchor(condition.getPage(), condition);
        page.getRecords().parallelStream().forEach(anchorStatisticsDTO -> addFinanceItem(anchorStatisticsDTO, condition.getStartDate(), condition.getEndDate()));
        return page;
    }

    /**
     * 保存主播用户关系
     *
     * @param request
     */
    public void saveAdminUserRelationAnchor(AnchorUserDeptDTO request) {
        if (request.getUserId() == null) {
            throw new AdminErrorException("用户id不能为空");
        }
        if (request.getAdminUserId() == null) {
            throw new AdminErrorException("后台用户id不能为空");
        }
        User user = userMapper.selectByPrimaryKey(request.getUserId());
        if (user == null) {
            throw new AdminErrorException("用户信息不存在");
        }
        LambdaQueryWrapper<AdminUserAnchorPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserAnchorPlus::getUserId, request.getUserId());
        Integer cnt = adminUserAnchorPlusMapper.selectCount(wrapper);
        if (cnt > 0) {
            throw new AdminErrorException("主播用户已被关联");
        } else {
            AdminUserAnchorPlus adminUserAnchorPlus = new AdminUserAnchorPlus();
            adminUserAnchorPlus.setUserId(request.getUserId());
            adminUserAnchorPlus.setAdminUserId(request.getAdminUserId());
            adminUserAnchorPlus.setCt(new Date());
            adminUserAnchorPlusMapper.insert(adminUserAnchorPlus);
        }
    }

    /**
     * 获取所有主播用户列表
     *
     * @return
     */
    public List<AnchorUserDTO> selectAllAnchorList() {
        return userMapper.selectAllAnchorList();
    }


    private void addItem(AnchorStatisticsDTO anchorStatisticsDTO, String startDate, String endDate) {
        anchorStatisticsDTO.setActiveUser(userMapper.findActiveUser(anchorStatisticsDTO.getUserId(), startDate, endDate)); //活跃用户
        anchorStatisticsDTO.setNewUser(userMapper.findNewUser(anchorStatisticsDTO.getUserId(), startDate, endDate)); //新用户
        anchorStatisticsDTO.setOldUser(userMapper.findOldUser(anchorStatisticsDTO.getUserId(), startDate)); //老用户
        anchorStatisticsDTO.setValidUser(userMapper.findValidUser(anchorStatisticsDTO.getUserId(), startDate, endDate)); //有效用户
        anchorStatisticsDTO.setChargeCount(orderRecordMapper.getRechargeCount(anchorStatisticsDTO.getUserId())); //活跃付费用户
        anchorStatisticsDTO.setExtensionCharge(userCommissionLogMapper.recommendCommission(anchorStatisticsDTO.getUserId(), startDate, endDate)); //推广用户充值
        if (anchorStatisticsDTO.getUserId() != null && anchorStatisticsDTO.getUserId().intValue() == UserConstants.UNCLASSIFIED_USER_ID) {
            BigDecimal extensionChargeOther = userCommissionLogMapper.recommendCommissionOther(startDate, endDate);
            BigDecimal extensionChargeSum = anchorStatisticsDTO.getExtensionCharge().add(extensionChargeOther);
            anchorStatisticsDTO.setExtensionCharge(extensionChargeSum);
        }
        anchorStatisticsDTO.setAnchorCharge(orderRecordMapper.anchorCharge(anchorStatisticsDTO.getUserId(), startDate, endDate)); //主播充值
    }

    private void addFinanceItem(AnchorStatisticsDTO anchorStatisticsDTO, String startDate, String endDate) {
        //推广用户充值
        anchorStatisticsDTO.setExtensionCharge(userCommissionLogMapper.recommendCommission(anchorStatisticsDTO.getUserId(), startDate, endDate));
        //主播充值
        anchorStatisticsDTO.setAnchorCharge(orderRecordMapper.anchorCharge(anchorStatisticsDTO.getUserId(), startDate, endDate));
    }

}
