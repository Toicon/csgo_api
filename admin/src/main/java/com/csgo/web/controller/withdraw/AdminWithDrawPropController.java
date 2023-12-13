package com.csgo.web.controller.withdraw;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.withdraw.SearchWithdrawPropCondition;
import com.csgo.domain.enums.WithdrawPropItemStatus;
import com.csgo.domain.enums.WithdrawPropStatus;
import com.csgo.domain.plus.withdraw.WithdrawPropDTO;
import com.csgo.domain.plus.withdraw.WithdrawPropRelate;
import com.csgo.domain.plus.withdraw.WithdrawPropRelateDTO;
import com.csgo.domain.user.UserMessage;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.UserMessageService;
import com.csgo.service.withdraw.WithdrawPropRelateService;
import com.csgo.service.withdraw.WithdrawPropService;
import com.csgo.support.DataConverter;
import com.csgo.support.StandardExceptionCode;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.withdraw.SearchWithdrawPropRequest;
import com.csgo.web.request.withdraw.WithdrawPropItemRequest;
import com.csgo.web.request.withdraw.WithdrawPropRequest;
import com.csgo.web.response.withdraw.WithdrawPropRelateResponse;
import com.csgo.web.response.withdraw.WithdrawPropResponse;
import com.csgo.web.support.Log;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/withdraw/prop")
@Slf4j
public class AdminWithDrawPropController extends BackOfficeController {

    @Autowired
    private WithdrawPropService withdrawPropService;
    @Autowired
    private WithdrawPropRelateService withdrawPropRelateService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;

    @PostMapping(value = "/pagination")
    @Log(desc = "查询用户提取列表")
    public PageResponse<WithdrawPropResponse> queryAll(@Valid @RequestBody SearchWithdrawPropRequest request) {
        SearchWithdrawPropCondition condition = DataConverter.to(SearchWithdrawPropCondition.class, request);
        Page<WithdrawPropDTO> pagination = withdrawPropService.pagination(condition);

        WithdrawPropRelateDTO withdrawAll = withdrawPropRelateService.findWithdrawAll(request.getUserName(), request.getFlag(), request.getStartDate(), request.getEndDate());

        Map<String, WithdrawPropRelateDTO> userRecharge = new HashMap<>();
        PageResponse<WithdrawPropResponse> responses = DataConverter.to(record -> {
            WithdrawPropResponse response = new WithdrawPropResponse();
            BeanUtils.copyProperties(record, response);
            response.setStatus(record.getStatus().name());
            response.setDescription(record.getStatus().getDescription());

            if (CollectionUtils.isEmpty(userRecharge) || !userRecharge.containsKey(record.getUserName())) {
                WithdrawPropRelateDTO userAmount = withdrawPropRelateService.findWithdrawAll(record.getUserName(), null, null, null);
                if (null != userAmount) {
                    userRecharge.put(record.getUserName(), userAmount);
                    response.setUserRecharge(userAmount.getRechargeAmount());
                    response.setUserPropAmount(userAmount.getUsdPrice());
                }
            } else if (!CollectionUtils.isEmpty(userRecharge)) {
                response.setUserRecharge(userRecharge.get(record.getUserName()).getRechargeAmount());
                response.setUserPropAmount(userRecharge.get(record.getUserName()).getUsdPrice());
            }
            return response;
        }, pagination);
        if (null != responses && responses.getData() != null && !CollectionUtils.isEmpty(responses.getData().getRows()) && null != withdrawAll) {
            responses.getData().getRows().get(0).setZbtPrice(withdrawAll.getUsdPrice());
            responses.getData().getRows().get(0).setRechargeAmount(withdrawAll.getRechargeAmount());
        }
        return responses;
    }

    @GetMapping("/item/{propId}")
    @Log(desc = "查看提取道具")
    public BaseResponse<List<WithdrawPropRelateResponse>> check(@PathVariable("propId") Integer propId) {
        List<WithdrawPropRelate> withdrawPropRelates = withdrawPropRelateService.findByPropId(propId);
        List<WithdrawPropRelateResponse> relateResponses = withdrawPropRelates.stream().map(relate -> {
            WithdrawPropRelateResponse relateResponse = new WithdrawPropRelateResponse();
            UserMessage userMessage = userMessageService.queryById(relate.getMessageId());
            relateResponse.setGiftProductName(userMessage.getProductName());
            relateResponse.setId(relate.getId());
            relateResponse.setPrice(userMessage.getMoney());
            relateResponse.setStatus(relate.getStatus().name());
            relateResponse.setDescription(relate.getStatus().getDescription());
            relateResponse.setMessage(relate.getMessage());
            return relateResponse;
        }).collect(Collectors.toList());
        return BaseResponse.<List<WithdrawPropRelateResponse>>builder().data(relateResponses).get();
    }

    @PutMapping("/batch")
    @Log(desc = "一键审批用户提取状态")
    public BaseResponse<Void> batchUpdate(@Valid @RequestBody WithdrawPropRequest request) {
        UserInfo userInfo = siteContext.getCurrentUser();
        if (StringUtils.hasText(redisTemplateFacde.get("ADMIN_WITHDRAW_" + userInfo.getId()))) {
            throw new ApiException(StandardExceptionCode.WITHDRAW_AUDIT_FAILURE, "当前道具已审批");
        }
        redisTemplateFacde.set("ADMIN_WITHDRAW_" + userInfo.getId(), JSON.toJSONString(request));
        try {
            withdrawPropService.updateBatchWithdraw(request.getId(), WithdrawPropStatus.valueOf(request.getStatus()), userInfo.getName());
        } finally {
            redisTemplateFacde.delete("ADMIN_WITHDRAW_" + userInfo.getId());
        }
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/item")
    @Log(desc = "审批用户提取状态")
    public BaseResponse<Void> updateItem(@Valid @RequestBody WithdrawPropItemRequest request) {
        UserInfo userInfo = siteContext.getCurrentUser();
        if (StringUtils.hasText(redisTemplateFacde.get("ADMIN_WITHDRAW_" + userInfo.getId()))) {
            throw new ApiException(StandardExceptionCode.WITHDRAW_AUDIT_FAILURE, "当前道具已审批");
        }
        redisTemplateFacde.set("ADMIN_WITHDRAW_" + userInfo.getId(), JSON.toJSONString(request));
        try {
            withdrawPropService.updateItem(request.getId(), WithdrawPropItemStatus.valueOf(request.getStatus()), userInfo.getName());
        } finally {
            redisTemplateFacde.delete("ADMIN_WITHDRAW_" + userInfo.getId());
        }
        return BaseResponse.<Void>builder().get();
    }
}
