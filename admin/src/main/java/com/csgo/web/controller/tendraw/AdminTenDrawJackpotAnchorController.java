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
@RequestMapping("/ten-draw/anchor-jackpot")
@RequiredArgsConstructor
public class AdminTenDrawJackpotAnchorController extends BackOfficeController {

    private final TenDrawJackpotService tenDrawJackpotService;
    private final SystemJackpotService systemJackpotService;

    private static final SystemJackpotTypeEnums ANCHOR_JACKPOT_TYPE = SystemJackpotTypeEnums.TEN_DRAW_ANCHOR;
    private static final JackpotType RECORD_TYPE = JackpotType.TEN_DRAW_ANCHOR;

    @GetMapping
    @Log(desc = "获取十连主播奖池")
    public BaseResponse<SystemJackpotVO> getJackpot() {
        BigDecimal balance = systemJackpotService.getJackpot(ANCHOR_JACKPOT_TYPE);
        BigDecimal spareBalance = systemJackpotService.loadSpareJackpot(ANCHOR_JACKPOT_TYPE);

        SystemJackpotVO vo = new SystemJackpotVO();
        vo.setBalance(balance);
        vo.setSpareBalance(spareBalance);

        return RUtil.ok(vo);
    }

    @PutMapping
    @Log(desc = "更新十连主播奖池")
    public BaseResponse<Void> update(@RequestParam("balance") BigDecimal balance) {
        String name = siteContext.getCurrentUser().getName();
        systemJackpotService.adminUpdateJackpot(ANCHOR_JACKPOT_TYPE, RECORD_TYPE, balance, name);
        return RUtil.ok();
    }

    @GetMapping("/pageList")
    @Log(desc = "十连奖池记录分页列表")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<TenDrawJackpotBillRecordDO> page = tenDrawJackpotService.pageList(ANCHOR_JACKPOT_TYPE, pageNum, pageSize);
        return new Result().result(page);
    }

}
