package com.csgo.modular.tendraw.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.framework.model.PageVO;
import com.csgo.framework.mybatis.util.MyBatisUtils;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.modular.tendraw.domain.TenDrawGameDO;
import com.csgo.modular.tendraw.domain.TenDrawGamePlayDO;
import com.csgo.modular.tendraw.mapper.TenDrawGameMapper;
import com.csgo.modular.tendraw.mapper.TenDrawGamePlayMapper;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawGameVM;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawGameVO;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawPlayGameVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTenDrawService {

    private final TenDrawGameMapper tenDrawGameMapper;
    private final TenDrawGamePlayMapper tenDrawGamePlayMapper;

    public PageVO<AdminTenDrawGameVO> pagination(AdminTenDrawGameVM vm) {
        Page<TenDrawGameDO> mpPage = MyBatisUtils.buildPage(vm);
        Page<AdminTenDrawGameVO> adminPage = tenDrawGameMapper.getAdminPage(mpPage, vm);
        return MyBatisUtils.toPageVO(adminPage);
    }

    public List<AdminTenDrawPlayGameVO> getPlayListByGameId(Integer gameId) {
        List<TenDrawGamePlayDO> list = tenDrawGamePlayMapper.listByGameId(gameId);
        return BeanCopyUtil.notNullMapList(list, AdminTenDrawPlayGameVO.class);
    }

}
