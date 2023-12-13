package com.csgo.web.controller.tendraw;

import com.csgo.framework.model.PageVO;
import com.csgo.framework.util.RUtil;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawGameVM;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawGameVO;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawPlayGameVO;
import com.csgo.modular.tendraw.service.AdminTenDrawService;
import com.csgo.web.controller.BackOfficeController;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author admin
 */
@Slf4j
@Api
@RequestMapping("/ten-draw")
@RequiredArgsConstructor
public class AdminTenDrawController extends BackOfficeController {

    private final AdminTenDrawService adminTenDrawService;

    @PostMapping("/pagination")
    public BaseResponse<PageVO<AdminTenDrawGameVO>> pagination(@Valid @RequestBody AdminTenDrawGameVM vm) {
        PageVO<AdminTenDrawGameVO> page = adminTenDrawService.pagination(vm);
        return RUtil.ok(page);
    }

    @GetMapping(value = "/{id}/play")
    public BaseResponse<List<AdminTenDrawPlayGameVO>> getPlayListByGameId(@PathVariable Integer id) {
        List<AdminTenDrawPlayGameVO> list = adminTenDrawService.getPlayListByGameId(id);
        return RUtil.ok(list);
    }

}
