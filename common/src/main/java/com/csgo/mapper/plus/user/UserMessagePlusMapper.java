package com.csgo.mapper.plus.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.SearchUserMessageCondition;
import com.csgo.domain.plus.message.MessageTurnDTO;
import com.csgo.domain.plus.user.UserMessageDTO;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.modular.product.enums.ProductKindEnums;
import com.csgo.modular.product.model.dto.UserMessageKeyStatisticsDTO;
import com.csgo.modular.product.model.front.ProductSimpleVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface UserMessagePlusMapper extends BaseMapper<UserMessagePlus> {

    /**
     * 获取最大奖励饰品
     */
    @Deprecated
    ProductSimpleVO getMaxPriceProduct(@Param("userId") Integer userId, @Param("startDate") Date startDate, @Param("giftType") String giftType);

    /**
     * 获取最大奖励饰品
     */
    ProductSimpleVO getMaxPriceProductBySource(@Param("userId") Integer userId, @Param("startDate") Date startDate, @Param("fromSource") Integer fromSource);

    /**
     * 获取开箱大奖励饰品
     */
    ProductSimpleVO getMaxPriceProductOfBox(@Param("userId") Integer userId, @Param("startDate") Date startDate, @Param("typeList") List<String> typeList);

    /**
     * 统计开箱次数
     */
    @Deprecated
    Integer getOpenBoxCount(@Param("userId") Integer userId, @Param("startDate") Date startDate, @Param("typeList") List<String> typeList);

    /**
     * 统计次数
     */
    Integer getCountBySource(@Param("userId") Integer userId, @Param("startDate") Date startDate, @Param("fromSource") Integer fromSource);

    BigDecimal getSumOpenBoxMoney(@Param("userId") Integer userId, @Param("startDate") Date startDate, @Param("typeList") List<String> typeList);

    Page<UserMessageDTO> pagination(IPage<UserMessageDTO> page, @Param("condition") SearchUserMessageCondition condition);

    BigDecimal countPrice(@Param("deptId") Integer deptId, @Param("endDate") String endDate);

    BigDecimal countPriceNotOwner(@Param("endDate") String endDate);

    List<MessageTurnDTO> findIdAndTurnId(@Param("roomNum") String roomNum);

    default List<UserMessagePlus> findByIds(List<Integer> userMessageIdList) {
        LambdaQueryWrapper<UserMessagePlus> wrapper = Wrappers.lambdaQuery();
        wrapper.in(UserMessagePlus::getId, userMessageIdList);
        return selectList(wrapper);
    }

    default List<UserMessagePlus> findByIdsAndUserId(List<Integer> userMessageIdList, int userId) {
        LambdaQueryWrapper<UserMessagePlus> wrapper = Wrappers.lambdaQuery();
        wrapper.in(UserMessagePlus::getId, userMessageIdList);
        wrapper.eq(UserMessagePlus::getUserId, userId);
        return selectList(wrapper);
    }

    default List<UserMessagePlus> findSellByIds(List<Integer> userMessageIdList, int userId, String status) {
        LambdaQueryWrapper<UserMessagePlus> wrapper = Wrappers.lambdaQuery();
        wrapper.in(UserMessagePlus::getId, userMessageIdList);
        wrapper.eq(UserMessagePlus::getUserId, userId);
        wrapper.eq(UserMessagePlus::getState, status);
        return selectList(wrapper);
    }

    default List<UserMessagePlus> findByUserId(int userId, String state) {
        LambdaQueryWrapper<UserMessagePlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserMessagePlus::getUserId, userId);
        wrapper.eq(UserMessagePlus::getState, state);
        return selectList(wrapper);
    }

    BigDecimal findWithdrawAmountByUserId(Integer userId);

    default List<UserMessagePlus> listUnUseGiftKeyProduct(Integer userId, Integer giftProductId, Integer size) {
        LambdaQueryWrapper<UserMessagePlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserMessagePlus::getState, 0);
        wrapper.eq(UserMessagePlus::getUserId, userId);
        wrapper.eq(UserMessagePlus::getGiftProductId, giftProductId);
        wrapper.eq(UserMessagePlus::getProductKind, ProductKindEnums.GIFT_KEY.getCode());
        wrapper.last(String.format("limit %s", size));
        return selectList(wrapper);
    }

    List<UserMessageKeyStatisticsDTO> getKeyStatistics(@Param("userId") Integer userId);

    default Integer countKeyByUserIdAndGiftProductId(@Param("userId") Integer userId, @Param("userId") Integer giftProductId) {
        LambdaQueryWrapper<UserMessagePlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserMessagePlus::getState, 0);
        wrapper.eq(UserMessagePlus::getGiftProductId, giftProductId);
        wrapper.eq(UserMessagePlus::getUserId, userId);
        wrapper.eq(UserMessagePlus::getProductKind, ProductKindEnums.GIFT_KEY.getCode());
        return selectCount(wrapper);
    }

    default UserMessagePlus selectLast() {
        LambdaQueryWrapper<UserMessagePlus> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByDesc(UserMessagePlus::getDrawDare);
        wrapper.last("limit 0,1");
        return selectOne(wrapper);
    }

    int deleteBeforeTime(@Param("flag") int flag, @Param("beforeTime") Date beforeTime);
}
