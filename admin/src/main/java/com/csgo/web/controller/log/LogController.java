package com.csgo.web.controller.log;

import com.csgo.condition.log.SearchLotteryDrawRecordCondition;
import com.csgo.condition.log.SearchLuckyProductDrawRecordCondition;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.lucky.LotteryDrawType;
import com.csgo.domain.user.User;
import com.csgo.service.GiftProductService;
import com.csgo.service.UserService;
import com.csgo.service.log.LogService;
import com.csgo.support.DataConverter;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.log.SearchLotteryDrawRecordRequest;
import com.csgo.web.request.log.SearchLuckyProductDrawRecordRequest;
import com.csgo.web.response.log.LotteryDrawRecordResponse;
import com.csgo.web.response.log.LuckyProductDrawRecordResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author admin
 */
@Api
@RequestMapping("/log")
public class LogController extends BackOfficeController {

    @Autowired
    private LogService logService;
    @Autowired
    private UserService userService;
    @Autowired
    private GiftProductService giftProductService;

    @PostMapping("/lucky-product/pagination")
    @Log(desc = "查询饰品升级抽奖列表")
    public PageResponse<LuckyProductDrawRecordResponse> luckyProductPagination(@Valid @RequestBody SearchLuckyProductDrawRecordRequest request) {
        SearchLuckyProductDrawRecordCondition condition = DataConverter.to(SearchLuckyProductDrawRecordCondition.class, request);
        if (!StringUtils.isEmpty(request.getUserName())) {
            User user = userService.getUserByUserName(request.getUserName());
            if (null != user) {
                condition.setUserId(user.getId());
            } else {
                condition.setUserId(-1);
            }
        }
        return DataConverter.to(record -> {
            LuckyProductDrawRecordResponse response = new LuckyProductDrawRecordResponse();
            BeanUtils.copyProperties(record, response);
            User queryUser = userService.getUserById(record.getUserId());
            if (queryUser != null) {
                response.setUserName(queryUser.getUserName());
            }
            return response;
        }, logService.pagination(condition));
    }

    @PostMapping("/lottery/pagination")
    @Log(desc = "查询抽奖日志列表")
    public PageResponse<LotteryDrawRecordResponse> pagination(@Valid @RequestBody SearchLotteryDrawRecordRequest request) {
        SearchLotteryDrawRecordCondition condition = DataConverter.to(SearchLotteryDrawRecordCondition.class, request);
        if (!StringUtils.isEmpty(request.getUserName())) {
            User user = userService.getUserByUserName(request.getUserName());
            if (null != user) {
                condition.setUserId(user.getId());
            } else {
                condition.setUserId(-1);
            }
        }
        if (StringUtils.hasText(request.getType())) {
            condition.setType(LotteryDrawType.valueOf(request.getType()));
        }
        return DataConverter.to(record -> {
            LotteryDrawRecordResponse response = new LotteryDrawRecordResponse();
            BeanUtils.copyProperties(record, response);
            User queryUser = userService.getUserById(record.getUserId());
            if (queryUser != null) {
                response.setUserName(queryUser.getUserName());
            }
            GiftProductPlus giftProductPlus = giftProductService.get(record.getHitGiftProductId());
            response.setHitGiftProductName(giftProductPlus == null ? null : giftProductPlus.getName());
            response.setType(type(record.getType()));
            return response;
        }, logService.pagination(condition));
    }

    private String type(LotteryDrawType type) {
        if (type == null) {
            return "";
        }
        if (LotteryDrawType.FORMULA.equals(type)) {
            return "公式抽奖";
        }
        if (LotteryDrawType.LUCKY.equals(type)) {
            return "幸运值抽奖";
        }
        if (LotteryDrawType.NORMAL.equals(type)) {
            return "正常开奖";
        }
        if (LotteryDrawType.DOWN.equals(type)) {
            return "降档开奖";
        }
        return "吸血抽奖";
    }
}
