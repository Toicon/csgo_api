package com.csgo.modular.bomb.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.model.PageVO;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.modular.bomb.domain.NovBombConfigDO;
import com.csgo.modular.bomb.mapper.NovBombConfigMapper;
import com.csgo.modular.bomb.model.admin.AdminNovBombConfigCreateVM;
import com.csgo.modular.bomb.model.admin.AdminNovBombConfigUpdateVM;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawBallPageVM;
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
public class AdminNovBombConfigService {

    private final NovBombConfigMapper novBombConfigMapper;

    public NovBombConfigDO selectById(Integer id) {
        return novBombConfigMapper.selectById(id);
    }

    public PageVO<NovBombConfigDO> getAdminPage(@Valid AdminTenDrawBallPageVM vm) {
        return novBombConfigMapper.getAdminPage(vm);
    }

    public void add(AdminNovBombConfigCreateVM vm) {
        NovBombConfigDO entity = BeanCopyUtil.notNullMap(vm, NovBombConfigDO.class);
        novBombConfigMapper.insert(entity);
    }

    public void update(AdminNovBombConfigUpdateVM vm) {
        NovBombConfigDO entity = novBombConfigMapper.selectById(vm.getId());
        if (entity == null) {
            throw BizServerException.of(CommonBizCode.COMMON_DATA_NOT_FOUND);
        }
        BeanCopyUtil.notNullMapTo(vm, entity);
        novBombConfigMapper.updateById(entity);
    }

    public void deleteById(Integer id) {
        novBombConfigMapper.deleteById(id);
    }

}
