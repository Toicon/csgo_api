package com.csgo.modular.tendraw.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.framework.model.PageVO;
import com.csgo.framework.mybatis.query.LambdaQueryWrapperX;
import com.csgo.framework.mybatis.util.MyBatisUtils;
import com.csgo.modular.tendraw.domain.TenDrawBallDO;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawBallPageVM;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface TenDrawBallMapper extends BaseMapper<TenDrawBallDO> {

    default List<TenDrawBallDO> listAll() {
        LambdaQueryWrapper<TenDrawBallDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(TenDrawBallDO::getId);
        return selectList(wrapper);
    }

    default PageVO<TenDrawBallDO> pagination(AdminTenDrawBallPageVM vm) {
        LambdaQueryWrapperX<TenDrawBallDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.orderByAsc(TenDrawBallDO::getId);

        Page<TenDrawBallDO> mpPage = MyBatisUtils.buildPage(vm);
        selectPage(mpPage, queryWrapper);
        return MyBatisUtils.toPageVO(mpPage);
    }

}
