package com.csgo.web.controller.jackpot;

import com.csgo.domain.plus.anchor.BoxAnchorJackpot;
import com.csgo.domain.plus.anchor.BoxAnchorJackpotBillRecord;
import com.csgo.domain.plus.anchor.BoxAnchorSpareJackpot;
import com.csgo.service.jackpot.BoxAnchorJackpotService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.response.anchor.BoxAnchorJackpotResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author admin
 */
@RestController
@RequestMapping("/box/anchor/jackpot")
public class BoxAnchorJackpotController extends BackOfficeController {
    @Autowired
    private BoxAnchorJackpotService boxAnchorJackpotService;

    @GetMapping
    @Log(desc = "获取奖池")
    public BaseResponse<BoxAnchorJackpotResponse> get() {
        BoxAnchorJackpot boxJackpot = boxAnchorJackpotService.getBoxJackpot();
        BoxAnchorSpareJackpot boxSpareJackpot = boxAnchorJackpotService.getBoxSpareJackpot();
        BoxAnchorJackpotResponse response = new BoxAnchorJackpotResponse();
        response.setBalance(boxJackpot.getBalance());
        response.setSpareBalance(boxSpareJackpot.getBalance());
        return BaseResponse.<BoxAnchorJackpotResponse>builder().data(response).get();
    }

    @PutMapping
    @Log(desc = "更新奖池")
    public BaseResponse<Void> update(@RequestParam("balance") BigDecimal balance) {
        boxAnchorJackpotService.updateBoxJackpot(balance, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("pageList")
    @ApiOperation("奖池记录分页列表")
    @Log(desc = "奖池记录分页列表")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<BoxAnchorJackpotBillRecord> boxJackpotBillRecordPageInfo = boxAnchorJackpotService.pageList(pageNum, pageSize);
        return new Result().result(boxJackpotBillRecordPageInfo);
    }
}
