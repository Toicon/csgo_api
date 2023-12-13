package com.csgo.web.controller.roll;

import com.csgo.condition.roll.SearchRollCoinsCondition;
import com.csgo.domain.plus.roll.RollCoins;
import com.csgo.service.UserService;
import com.csgo.service.roll.RollCoinsService;
import com.csgo.service.roll.RollHelp;
import com.csgo.support.DataConverter;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.roll.BatchDeleteRollCoinsRequest;
import com.csgo.web.request.roll.InsertRollCoinsRequest;
import com.csgo.web.request.roll.SearchRollCoinsRequest;
import com.csgo.web.response.roll.RollCoinsResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Api
@RequestMapping("/roll/coins")
@Slf4j
public class AdminRollCoinsController extends BackOfficeController {

    @Autowired
    private RollCoinsService rollcoinsService;
    @Autowired
    private UserService userService;
    @Autowired
    private RollHelp rollHelp;

    @PostMapping
    @Log(desc = "新增roll房金币")
    public BaseResponse<Void> insert(@Valid @RequestBody InsertRollCoinsRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new ApiException(StandardExceptionCode.ROLL_COIN_AMOUNT_INVALID, "金币输入不合法");
        }
        List<RollCoins> rollCoinsList = new ArrayList<>();
        for (int i = 0; i < request.getNum(); i++) {
            RollCoins rollCoins = new RollCoins();
            rollCoins.setAmount(request.getAmount());
            rollCoins.setRollId(request.getRollId());
            rollCoins.setImg(request.getImg());
            rollCoinsList.add(rollCoins);
        }
        rollcoinsService.insert(rollCoinsList);
        rollHelp.setRollInfo(request.getRollId());
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 查询Roll金币信息
     *
     * @return
     */
    @PostMapping("/pagination")
    @Log(desc = "查询roll房金币列表")
    public PageResponse<RollCoinsResponse> pagination(@Valid @RequestBody SearchRollCoinsRequest request) {
        return DataConverter.to(rollCoins -> {
            RollCoinsResponse response = new RollCoinsResponse();
            BeanUtils.copyProperties(rollCoins, response);
            if (null != rollCoins.getUserId()) {
                response.setUserName(userService.getUserById(rollCoins.getUserId()).getUserName());
            }
            return response;
        }, rollcoinsService.pagination(DataConverter.to(SearchRollCoinsCondition.class, request)));
    }

    @PutMapping("/{id}")
    @Log(desc = "修改roll房金币")
    public BaseResponse<Void> delete(@PathVariable("id") int id, @Valid @RequestBody InsertRollCoinsRequest request) {
        RollCoins rollCoins = rollcoinsService.get(id);
        BeanUtilsEx.copyProperties(request, rollCoins);
        rollcoinsService.update(rollCoins);
        rollHelp.setRollInfo(request.getRollId());
        return BaseResponse.<Void>builder().get();
    }

    @DeleteMapping
    public BaseResponse<Void> delete(@Valid @RequestBody BatchDeleteRollCoinsRequest request) {
        RollCoins coins = rollcoinsService.get(request.getIds().get(0));
        rollcoinsService.batchDelete(request.getIds());
        rollHelp.setRollInfo(coins.getRollId());
        return BaseResponse.<Void>builder().get();
    }

    @DeleteMapping("/{id}")
    @Log(desc = "删除roll房金币")
    public BaseResponse<Void> delete(@PathVariable("id") int id) {
        RollCoins coins = rollcoinsService.get(id);
        rollcoinsService.delete(coins.getId());
        rollHelp.setRollInfo(coins.getRollId());
        return BaseResponse.<Void>builder().get();
    }
}
