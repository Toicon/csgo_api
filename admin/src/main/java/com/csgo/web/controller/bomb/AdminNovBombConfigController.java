package com.csgo.web.controller.bomb;

import com.csgo.framework.model.PageVO;
import com.csgo.framework.util.RUtil;
import com.csgo.modular.bomb.domain.NovBombConfigDO;
import com.csgo.modular.bomb.model.admin.AdminNovBombConfigCreateVM;
import com.csgo.modular.bomb.model.admin.AdminNovBombConfigUpdateVM;
import com.csgo.modular.bomb.service.AdminNovBombConfigService;
import com.csgo.modular.tendraw.domain.TenDrawBallDO;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawBallPageVM;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawBallUpdateVM;
import com.csgo.modular.tendraw.service.AdminTenDrawBallService;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
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
@RequestMapping("/nov-bomb/config")
@RequiredArgsConstructor
public class AdminNovBombConfigController extends BackOfficeController {

    private final AdminNovBombConfigService adminNovBombConfigService;

    @PostMapping("/pagination")
    public BaseResponse<PageVO<NovBombConfigDO>> pagination(@Valid @RequestBody AdminTenDrawBallPageVM vm) {
        PageVO<NovBombConfigDO> page = adminNovBombConfigService.getAdminPage(vm);
        return RUtil.ok(page);
    }

    @GetMapping(value = "/{id}")
    public BaseResponse<NovBombConfigDO> get(@PathVariable Integer id) {
        NovBombConfigDO entity = adminNovBombConfigService.selectById(id);
        return RUtil.ok(entity);
    }

    @PostMapping("/add")
    @Log(desc = "新增拆弹")
    public BaseResponse<Void> add(@Valid @RequestBody AdminNovBombConfigCreateVM vm) {
        Integer opUserId = siteContext.getCurrentUser().getId();
        log.info("[新增拆弹] data:{} opUserId:{}", vm, opUserId);
        adminNovBombConfigService.add(vm);
        return RUtil.ok();
    }

    @PutMapping("/update")
    @Log(desc = "更新拆弹")
    public BaseResponse<Void> update(@Valid @RequestBody AdminNovBombConfigUpdateVM vm) {
        Integer opUserId = siteContext.getCurrentUser().getId();
        log.info("[更新拆弹] data:{} opUserId:{}", vm, opUserId);
        adminNovBombConfigService.update(vm);
        return RUtil.ok();
    }

    @DeleteMapping("/{id}")
    @Log(desc = "删除拆弹")
    public BaseResponse<Void> deleteById(@PathVariable Integer id) {
        Integer opUserId = siteContext.getCurrentUser().getId();
        log.info("[删除拆弹] id:{} opUserId:{}", id, opUserId);
        adminNovBombConfigService.deleteById(id);
        return RUtil.ok();
    }

}
