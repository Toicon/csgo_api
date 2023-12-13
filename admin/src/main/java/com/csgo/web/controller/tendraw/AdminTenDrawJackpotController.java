package com.csgo.web.controller.tendraw;

import com.csgo.domain.plus.jackpot.JackpotType;
import com.csgo.framework.util.RUtil;
import com.csgo.modular.system.enums.SystemJackpotTypeEnums;
import com.csgo.modular.system.model.admin.SystemJackpotVO;
import com.csgo.modular.system.service.SystemJackpotService;
import com.csgo.modular.tendraw.domain.TenDrawJackpotBillRecordDO;
import com.csgo.modular.tendraw.service.system.TenDrawJackpotService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author admin
 */
@RestController
@RequestMapping("/ten-draw/jackpot")
@RequiredArgsConstructor
public class AdminTenDrawJackpotController extends BackOfficeController {

    private final TenDrawJackpotService tenDrawJackpotService;
    private final SystemJackpotService systemJackpotService;

    private static final SystemJackpotTypeEnums JACKPOT_TYPE = SystemJackpotTypeEnums.TEN_DRAW;
    private static final JackpotType RECORD_TYPE = JackpotType.TEN_DRAW;

    @GetMapping
    @Log(desc = "获取十连奖池")
    public BaseResponse<SystemJackpotVO> getJackpot() {
        BigDecimal balance = systemJackpotService.getJackpot(JACKPOT_TYPE);
        BigDecimal spareBalance = systemJackpotService.loadSpareJackpot(JACKPOT_TYPE);

        SystemJackpotVO vo = new SystemJackpotVO();
        vo.setBalance(balance);
        vo.setSpareBalance(spareBalance);

        return RUtil.ok(vo);
    }

    @PutMapping
    @Log(desc = "更新十连奖池")
    public BaseResponse<Void> update(@RequestParam("balance") BigDecimal balance) {
        String name = siteContext.getCurrentUser().getName();
        systemJackpotService.adminUpdateJackpot(JACKPOT_TYPE, RECORD_TYPE, balance, name);
        return RUtil.ok();
    }

    @GetMapping("/pageList")
    @Log(desc = "十连奖池记录分页列表")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<TenDrawJackpotBillRecordDO> page = tenDrawJackpotService.pageList(JACKPOT_TYPE, pageNum, pageSize);
        return new Result().result(page);
    }

}
