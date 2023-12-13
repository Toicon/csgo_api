package com.csgo.modular.bomb.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.framework.model.PageParam;
import com.csgo.framework.model.PageVO;
import com.csgo.framework.mybatis.query.LambdaQueryWrapperX;
import com.csgo.framework.mybatis.util.MyBatisUtils;
import com.csgo.modular.bomb.domain.NovBombConfigDO;
import com.csgo.modular.tendraw.domain.TenDrawBallDO;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawBallPageVM;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface NovBombConfigMapper extends BaseMapper<NovBombConfigDO> {

    default List<NovBombConfigDO> listAll() {
        LambdaQueryWrapper<NovBombConfigDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombConfigDO::getHidden, false);
        wrapper.orderByDesc(NovBombConfigDO::getSortId);
        return selectList(wrapper);
    }

    default PageVO<NovBombConfigDO> getAdminPage(PageParam vm) {
        LambdaQueryWrapperX<NovBombConfigDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.orderByDesc(NovBombConfigDO::getSortId);

        Page<NovBombConfigDO> mpPage = MyBatisUtils.buildPage(vm);
        selectPage(mpPage, wrapper);
        return MyBatisUtils.toPageVO(mpPage);
    }

}
