package com.csgo.modular.tendraw.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.model.PageVO;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.modular.tendraw.domain.TenDrawBallDO;
import com.csgo.modular.tendraw.mapper.TenDrawBallMapper;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawBallPageVM;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawBallUpdateVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTenDrawBallService {

    private final TenDrawBallMapper tenDrawBallMapper;

    public TenDrawBallDO selectById(Integer id) {
        return tenDrawBallMapper.selectById(id);
    }

    public void deleteById(Integer id) {
        tenDrawBallMapper.deleteById(id);
    }

    public void update(AdminTenDrawBallUpdateVM vm) {
        TenDrawBallDO entity = tenDrawBallMapper.selectById(vm.getId());
        if (entity == null) {
            throw BizServerException.of(CommonBizCode.COMMON_DATA_NOT_FOUND);
        }

        BeanCopyUtil.notNullMapTo(vm, entity);
        tenDrawBallMapper.updateById(entity);
    }

    public PageVO<TenDrawBallDO> pagination(@Valid AdminTenDrawBallPageVM vm) {
        return tenDrawBallMapper.pagination(vm);
    }

}
