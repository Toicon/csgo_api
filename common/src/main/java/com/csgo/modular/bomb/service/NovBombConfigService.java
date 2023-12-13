package com.csgo.modular.bomb.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.modular.bomb.domain.NovBombConfigDO;
import com.csgo.modular.bomb.mapper.NovBombConfigMapper;
import com.csgo.modular.bomb.model.front.NovBombConfigVO;
import com.csgo.modular.bomb.model.front.NovBombHomeVO;
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
public class NovBombConfigService {

    private static final Integer MAX_SIZE = 5;

    private final NovBombConfigMapper novBombConfigMapper;

    public NovBombHomeVO getConfig() {
        List<NovBombConfigDO> list = novBombConfigMapper.listAll();

        List<NovBombConfigVO> voList = BeanCopyUtil.mapList(list, NovBombConfigVO.class);

        NovBombHomeVO vo = new NovBombHomeVO();
        vo.setList(voList);
        vo.setMaxSize(MAX_SIZE);
        return vo;
    }

    public NovBombConfigDO loadUnHiddenConfig(Integer id) {
        NovBombConfigDO config = loadConfig(id);
        if (config.getHidden()) {
            log.error("[模拟拆弹] id:{} 已经被隐藏", id);
            throw BizServerException.of(CommonBizCode.COMMON_DATA_NOT_FOUND);
        }
        return config;
    }

    public NovBombConfigDO loadConfig(Integer id) {
        NovBombConfigDO config = novBombConfigMapper.selectById(id);
        if (config == null) {
            throw BizServerException.of(CommonBizCode.COMMON_DATA_NOT_FOUND);
        }
        if (config.getProductPriceParams() == null) {
            log.error("[模拟拆弹] id:{} 饰品倍场参数为空", id);
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        if (config.getPrice() == null) {
            log.error("[模拟拆弹] id:{} 饰品价格配置为空", id);
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        if (config.getLowPrice() == null || config.getHighPrice() == null) {
            log.error("[模拟拆弹] id:{} 饰品上下限配置错误", id);
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        return config;
    }

}
