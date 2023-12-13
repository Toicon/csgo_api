package com.csgo.mapper.plus.shop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchUserCurrencyExchangeCondition;
import com.csgo.domain.plus.user.UserCurrencyExchange;
import org.springframework.stereotype.Repository;

/**
 * 用户钻石兑换记录
 *
 * @author admin
 */
@Repository
public interface UserCurrencyExchangeMapper extends BaseMapper<UserCurrencyExchange> {
    /**
     * 用户钻石兑换记录
     *
     * @param condition
     * @return
     */
    default Page<UserCurrencyExchange> pagination(SearchUserCurrencyExchangeCondition condition) {
        LambdaQueryWrapper<UserCurrencyExchange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCurrencyExchange::getUserId, condition.getUserId());
        wrapper.orderByDesc(UserCurrencyExchange::getCreateDate);
        return selectPage(condition.getPage(), wrapper);
    }
}
