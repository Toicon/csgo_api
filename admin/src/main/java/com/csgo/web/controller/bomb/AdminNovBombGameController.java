package com.csgo.web.controller.bomb;

import com.csgo.framework.model.PageVO;
import com.csgo.framework.util.RUtil;
import com.csgo.modular.bomb.model.admin.AdminNovBombGamePlayVO;
import com.csgo.modular.bomb.model.admin.AdminNovBombGameVM;
import com.csgo.modular.bomb.model.admin.AdminNovBombGameVO;
import com.csgo.modular.bomb.service.AdminNovBombGameService;
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
@RequestMapping("/nov-bomb/game")
@RequiredArgsConstructor
public class AdminNovBombGameController extends BackOfficeController {

    private final AdminNovBombGameService adminNovBombGameService;

    @PostMapping("/pagination")
    public BaseResponse<PageVO<AdminNovBombGameVO>> pagination(@Valid @RequestBody AdminNovBombGameVM vm) {
        PageVO<AdminNovBombGameVO> page = adminNovBombGameService.pagination(vm);
        return RUtil.ok(page);
    }

    @GetMapping(value = "/{id}/play")
    public BaseResponse<List<AdminNovBombGamePlayVO>> getPlayListByGameId(@PathVariable Integer id) {
        List<AdminNovBombGamePlayVO> list = adminNovBombGameService.getPlayListByGameId(id);
        return RUtil.ok(list);
    }

}
