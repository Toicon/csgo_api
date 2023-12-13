package com.csgo.mapper.plus.ali;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.ali.AliAppMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 支付宝小程序菜单表
 *
 */
@Repository
public interface AliAppMenuMapper extends BaseMapper<AliAppMenu> {
    default List<AliAppMenu> findAppMenu(){
        LambdaQueryWrapper<AliAppMenu> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AliAppMenu::getDisplayState, YesOrNoEnum.NO.getCode());
        wrapper.orderByDesc(AliAppMenu::getCreateDate);
        return selectList(wrapper);
    }
}
