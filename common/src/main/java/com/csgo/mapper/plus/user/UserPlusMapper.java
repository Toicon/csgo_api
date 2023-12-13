package com.csgo.mapper.plus.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserPlusCondition;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.user.UserPlusDTO;
import com.csgo.domain.report.StatisticsDTO;
import com.csgo.domain.report.UserBalanceDTO;
import com.csgo.model.StatisticsUserBalanceDTO;
import com.csgo.model.StatisticsUserCountDTO;
import com.csgo.support.GlobalConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface UserPlusMapper extends BaseMapper<UserPlus> {

    @Update("update user set balance = balance + #{amount} where id= #{userId}")
    boolean addBalance(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);

    @Update("update user set balance = balance - #{amount} where id= #{userId} and balance - #{amount} >= 0 ")
    boolean subBalance(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);

    @Update("update user set diamond_balance = diamond_balance + #{amount} where id= #{userId}")
    boolean addDiamondBalance(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);

    @Update("update user set diamond_balance = diamond_balance - #{amount} where id= #{userId} and diamond_balance - #{amount} >= 0 ")
    boolean subDiamondBalance(@Param("userId") Integer userId, @Param("amount") BigDecimal amount);

    Page<UserPlusDTO> pagination(IPage<UserPlus> page, @Param("condition") SearchUserPlusCondition condition);

    default List<UserPlus> findAllInsideUser(String userName) {
        LambdaQueryWrapper<UserPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(UserPlus::getUserName, "%" + userName + "%");
        wrapper.eq(UserPlus::getFlag, GlobalConstants.INTERNAL_USER_FLAG);
        return selectList(wrapper);
    }

    /**
     * 当日新增用户
     *
     * @param startDate
     * @param endDate
     * @param dataScope
     * @return
     */
    List<StatisticsDTO> countAddUser(String startDate, String endDate, String dataScope);


    int countOldUser(@Param("deptId") Integer deptId, @Param("startDate") String startDate);

    List<StatisticsUserCountDTO> countOldUserByCreateDate(@Param("createDate") String createDate);

    int countOldUserNotOwner(@Param("startDate") String startDate);

    List<UserPlus> findByRoomNum(String roomNum);

    default UserPlus getBySteamId(String steamId) {
        LambdaQueryWrapper<UserPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserPlus::getSteamId, steamId);
        return selectOne(wrapper);
    }

    default List<UserPlus> findByIds(Collection<Integer> ids) {
        LambdaQueryWrapper<UserPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.in(UserPlus::getId, ids);
        return selectList(wrapper);
    }

    default int countRecommend(@Param("parentId") Integer parentId) {
        LambdaQueryWrapper<UserPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserPlus::getParentId, parentId);
        wrapper.eq(UserPlus::getFlag, GlobalConstants.RETAIL_USER_FLAG);
        return selectCount(wrapper);
    }

    UserBalanceDTO countBalance(@Param("deptId") Integer deptId, @Param("endDate") String endDate);

    List<StatisticsUserBalanceDTO> countBalanceByCreateDate(@Param("createDate") String createDate);

    UserBalanceDTO countBalanceNotOwner(@Param("endDate") String endDate);

    default int countUserNum(Date startDate, Date endDate) {
        LambdaQueryWrapper<UserPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserPlus::getFlag, GlobalConstants.RETAIL_USER_FLAG);
        if (startDate != null) {
            wrapper.gt(UserPlus::getCreatedAt, startDate);
        }
        if (endDate != null) {
            wrapper.le(UserPlus::getCreatedAt, endDate);
        }
        return selectCount(wrapper);
    }

    /**
     * 根据身份证id获取用户列表
     *
     * @param idNo
     * @return
     */
    default List<UserPlus> findByIdNo(String idNo) {
        LambdaQueryWrapper<UserPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserPlus::getIdNo, idNo);
        return selectList(wrapper);
    }

    /**
     * 获取用户昵称数量
     *
     * @param userId
     * @param name
     * @return
     */
    default int getNameCount(Integer userId, String name) {
        LambdaQueryWrapper<UserPlus> wrapper = Wrappers.lambdaQuery();
        if (userId != null) {
            wrapper.ne(UserPlus::getId, userId);
        }
        wrapper.eq(UserPlus::getName, name);
        return selectCount(wrapper);
    }

    /**
     * 获取Steam个数
     *
     * @param userId
     * @param steam
     * @return
     */
    default int getSteamCount(Integer userId, String steam) {
        LambdaQueryWrapper<UserPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.ne(UserPlus::getId, userId);
        wrapper.eq(UserPlus::getSteam, steam);
        return selectCount(wrapper);
    }

    /**
     * 根据身份证号码获取实名认证通过列表
     *
     * @param idNo
     * @return
     */
    default List<UserPlus> findRealNameAuthByIdNo(String idNo) {
        LambdaQueryWrapper<UserPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserPlus::getRealNameState, YesOrNoEnum.YES.getCode());
        wrapper.eq(UserPlus::getIdNo, idNo);
        return selectList(wrapper);
    }

}
