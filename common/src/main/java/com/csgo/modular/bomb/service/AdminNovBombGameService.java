package com.csgo.modular.bomb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.framework.model.PageVO;
import com.csgo.framework.mybatis.util.MyBatisUtils;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.modular.bomb.domain.NovBombGameDO;
import com.csgo.modular.bomb.domain.NovBombGamePlayDO;
import com.csgo.modular.bomb.mapper.NovBombGameMapper;
import com.csgo.modular.bomb.mapper.NovBombGamePlayMapper;
import com.csgo.modular.bomb.model.admin.AdminNovBombGamePlayVO;
import com.csgo.modular.bomb.model.admin.AdminNovBombGameVM;
import com.csgo.modular.bomb.model.admin.AdminNovBombGameVO;
import com.csgo.modular.tendraw.domain.TenDrawGameDO;
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
public class AdminNovBombGameService {

    private final NovBombGameMapper novBombGameMapper;
    private final NovBombGamePlayMapper novBombGamePlayMapper;

    public PageVO<AdminNovBombGameVO> pagination(AdminNovBombGameVM vm) {
        Page<NovBombGameDO> mpPage = MyBatisUtils.buildPage(vm);
        Page<AdminNovBombGameVO> adminPage = novBombGameMapper.getAdminPage(mpPage, vm);
        return MyBatisUtils.toPageVO(adminPage);
    }

    public List<AdminNovBombGamePlayVO> getPlayListByGameId(Integer gameId) {
        List<NovBombGamePlayDO> list = novBombGamePlayMapper.listByGameId(gameId);
        return BeanCopyUtil.notNullMapList(list, AdminNovBombGamePlayVO.class);
    }

}
