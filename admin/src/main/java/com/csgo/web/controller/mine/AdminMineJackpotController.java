package com.csgo.web.controller.mine;

import com.csgo.domain.plus.mine.MineJackpot;
import com.csgo.domain.plus.mine.MineJackpotBillRecord;
import com.csgo.domain.plus.mine.MineSpareJackpot;
import com.csgo.service.mine.MineJackpotService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.response.mine.MineJackpotResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 扫雷活动
 *
 * @author admin
 */
@RestController
@RequestMapping("/mine/jackpot")
public class AdminMineJackpotController extends BackOfficeController {
    @Autowired
    private MineJackpotService mineJackpotService;

    @GetMapping
    @Log(desc = "获取扫雷奖池")
    public BaseResponse<MineJackpotResponse> get() {
        MineJackpot boxJackpot = mineJackpotService.getMineJackpot();
        MineSpareJackpot boxSpareJackpot = mineJackpotService.getMineSpareJackpot();
        MineJackpotResponse response = new MineJackpotResponse();
        if (boxJackpot != null) {
            response.setBalance(boxJackpot.getBalance());
        } else {
            response.setBalance(BigDecimal.ZERO);
        }
        if (boxSpareJackpot != null) {
            response.setSpareBalance(boxSpareJackpot.getBalance());
        } else {
            response.setSpareBalance(BigDecimal.ZERO);
        }
        return BaseResponse.<MineJackpotResponse>builder().data(response).get();
    }

    @PutMapping
    @Log(desc = "更新扫雷奖池")
    public BaseResponse<Void> update(@RequestParam("balance") BigDecimal balance) {
        mineJackpotService.updateMineJackpot(balance, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("pageList")
    @Log(desc = "扫雷奖池记录分页列表")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<MineJackpotBillRecord> mineJackpotBillRecordPageInfo = mineJackpotService.pageList(pageNum, pageSize);
        return new Result().result(mineJackpotBillRecordPageInfo);
    }
}
