package com.csgo.service.envelop;

import com.csgo.domain.plus.envelop.RedEnvelop;
import com.csgo.mapper.plus.envelop.RedEnvelopMapper;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.exception.ApiException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author admin
 */
@Service
public class RedEnvelopSortService {

    @Autowired
    private RedEnvelopMapper redEnvelopMapper;

    @Autowired
    protected SiteContext siteContext;

    /**
     * 更新红包排序值
     */
    public void updateSortId(Integer id, Integer sort) {
        String username = siteContext.getCurrentUser().getUsername();

        RedEnvelop redEnvelop = redEnvelopMapper.selectById(id);
        if (redEnvelop == null) {
            throw new ApiException(HttpStatus.SC_NOT_FOUND, "红包信息不存在");
        }
        redEnvelop.setSortId(sort != null ? sort : 0);
        redEnvelop.setUpdateDate(new Date());
        redEnvelop.setUpdateBy(username);
        redEnvelopMapper.updateById(redEnvelop);
    }

}
