package com.csgo.mapper.plus.hee;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.enums.TradeStatusEnum;
import com.csgo.domain.plus.hee.UserPayHee;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 汇付宝支付用户签约信息
 *
 * @author admin
 */
@Repository
public interface UserPayHeeMapper extends BaseMapper<UserPayHee> {


    /**
     * 根据用户id和绑卡列表ID获取签约信息
     *
     * @param userId
     * @return
     */
    default UserPayHee getByIdAndUserId(Integer id, Integer userId) {
        LambdaQueryWrapper<UserPayHee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPayHee::getId, id);
        wrapper.eq(UserPayHee::getUserId, userId);
        return selectOne(wrapper);
    }

    /**
     * 根据用户id获取签约信息
     *
     * @param outTradeNo
     * @return
     */
    default UserPayHee getByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<UserPayHee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPayHee::getOutTradeNo, outTradeNo);
        return selectOne(wrapper);
    }


    /**
     * 根据用户id获取签约信息列表
     *
     * @param userId
     * @return
     */
    default List<UserPayHee> findByUserId(Integer userId) {
        LambdaQueryWrapper<UserPayHee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPayHee::getUserId, userId);
        wrapper.eq(UserPayHee::getTradeStatus, TradeStatusEnum.YES.getCode());
        return selectList(wrapper);
    }

    /**
     * 获取签约中数据以及持卡人身份证号为空的数据
     *
     * @return
     */
    default List<UserPayHee> findAll() {
        LambdaQueryWrapper<UserPayHee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPayHee::getTradeStatus, TradeStatusEnum.INIT.getCode());
        wrapper.or();
        wrapper.isNull(UserPayHee::getCertNo);
        return selectList(wrapper);
    }
}
