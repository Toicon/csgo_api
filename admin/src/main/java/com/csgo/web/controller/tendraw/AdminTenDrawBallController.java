package com.csgo.web.controller.tendraw;

import com.csgo.framework.model.PageVO;
import com.csgo.framework.security.SecurityContext;
import com.csgo.framework.security.model.SecurityUser;
import com.csgo.framework.util.RUtil;
import com.csgo.modular.tendraw.domain.TenDrawBallDO;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawBallPageVM;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawBallUpdateVM;
import com.csgo.modular.tendraw.service.AdminTenDrawBallService;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import com.echo.framework.platform.interceptor.session.SessionContext;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author admin
 */
@Slf4j
@Api
@RequestMapping("/ten-draw/ball")
@RequiredArgsConstructor
public class AdminTenDrawBallController extends BackOfficeController {

    private final AdminTenDrawBallService adminTenDrawBallService;

    @PostMapping("/pagination")
    public BaseResponse<PageVO<TenDrawBallDO>> pagination(@Valid @RequestBody AdminTenDrawBallPageVM vm) {
        PageVO<TenDrawBallDO> page = adminTenDrawBallService.pagination(vm);
        return RUtil.ok(page);
    }

    @GetMapping(value = "/{id}")
    public BaseResponse<TenDrawBallDO> get(@PathVariable Integer id) {
        TenDrawBallDO entity = adminTenDrawBallService.selectById(id);
        return RUtil.ok(entity);
    }

    @PutMapping("/update")
    @Log(desc = "更新十连魂球")
    public BaseResponse<Void> update(@Valid @RequestBody AdminTenDrawBallUpdateVM vm) {
        Integer opUserId = siteContext.getCurrentUser().getId();
        log.info("[更新十连魂球] data:{} opUserId:{}", vm, opUserId);
        adminTenDrawBallService.update(vm);
        return RUtil.ok();
    }

    @DeleteMapping("/{id}")
    @Log(desc = "删除十连魂球")
    public BaseResponse<Void> deleteById(@PathVariable Integer id) {
        Integer opUserId = siteContext.getCurrentUser().getId();
        log.info("[更新十连魂球] id:{} opUserId:{}", id, opUserId);
        adminTenDrawBallService.deleteById(id);
        return RUtil.ok();
    }

}
