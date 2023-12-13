package com.csgo.modular.bomb.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.framework.cache.RedisCacheLogic;
import com.csgo.framework.exception.BizClientException;
import com.csgo.modular.bomb.domain.NovBombConfigDO;
import com.csgo.modular.bomb.model.front.NovBombPreviewVO;
import com.csgo.modular.bomb.model.front.NovBombProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovBombPreviewService {

    private static final String DATA_BOMB_PREVIEW_KEY = "data:bomb:%s";

    private final RedisCacheLogic redisCacheLogic;

    private final NovBombConfigService novBombConfigService;
    private final NovBombProductService novBombProductService;

    public NovBombPreviewVO preview(Integer configId) {
        NovBombConfigDO config = novBombConfigService.loadUnHiddenConfig(configId);

        List<NovBombProductVO> voList = novBombProductService.getProduct(config, config.getPrice());

        // 返回
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        NovBombPreviewVO vo = new NovBombPreviewVO();
        vo.setConfigId(config.getId());
        vo.setPrice(config.getPrice());
        vo.setLowPrice(config.getLowPrice());
        vo.setHighPrice(config.getHighPrice());
        vo.setMaxTimes(config.getMaxTimes());
        vo.setUuid(uuid);
        vo.setProductList(voList);

        String key = getPreviewKey(uuid);
        redisCacheLogic.set(key, vo, 3, TimeUnit.HOURS);
        return vo;
    }

    public NovBombPreviewVO getPreview(String uuid) {
        String key = getPreviewKey(uuid);
        return redisCacheLogic.get(key, NovBombPreviewVO.class)
                .orElseThrow(() -> BizClientException.of(CommonBizCode.NOV_BOMB_DATA_INVALID));
    }

    private String getPreviewKey(String uuid) {
        return String.format(DATA_BOMB_PREVIEW_KEY, uuid);
    }

}
