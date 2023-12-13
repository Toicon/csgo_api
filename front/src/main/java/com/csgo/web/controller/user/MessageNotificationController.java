package com.csgo.web.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.notification.SearchMessageNotificationCondition;
import com.csgo.domain.plus.message.MessageNotification;
import com.csgo.domain.enums.NotificationStatusEnum;
import com.csgo.service.MessageNotificationService;
import com.csgo.support.DataConverter;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.user.SearchMessageNotificationRequest;
import com.csgo.web.response.user.MessageNotificationResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @ClassName: MessageNotificationController
 * @description: 消息通知
 * @author: Andy
 */
@Api
@LoginRequired
@RequireSession
@RequestMapping("/message/notification")
public class MessageNotificationController {

    @Autowired
    private MessageNotificationService messageNotificationService;
    @Autowired
    private SiteContext siteContext;

    @PutMapping
    public BaseResponse<Void> read(@RequestParam(value = "id", required = false) Integer id) {
        int userId = siteContext.getCurrentUser().getId();
        messageNotificationService.updateStatus(userId, id, NotificationStatusEnum.READ);
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("unread-count")
    public BaseResponse<Integer> unreadCount() {
        int userId = siteContext.getCurrentUser().getId();
        int count = messageNotificationService.count(userId, "UNREAD");
        return BaseResponse.<Integer>builder().data(count).get();
    }

    /**
     * 查询所有对应的消息通知
     *
     * @return
     */
    @ApiOperation("查询所有对应的消息通知")
    @PostMapping(value = "pagination")
    public PageResponse<MessageNotificationResponse> pagination(@Valid @RequestBody SearchMessageNotificationRequest request) {
        SearchMessageNotificationCondition condition = DataConverter.to(SearchMessageNotificationCondition.class, request);
        condition.setUserId(siteContext.getCurrentUser().getId());
        Page<MessageNotification> pagination = messageNotificationService.pagination(condition);
        return DataConverter.to(record -> {
            MessageNotificationResponse response = new MessageNotificationResponse();
            BeanUtils.copyProperties(record, response);
            response.setStatus(record.getStatus().name());
            return response;
        }, pagination);
    }


    /**
     * 根据ID删除对应的消息通知
     *
     * @return
     */
    @ApiOperation("根据ID删除对应的消息通知")
    @DeleteMapping("{id}")
    public BaseResponse<Void> delete(@PathVariable("id") int id) {
        messageNotificationService.delete(id);
        return BaseResponse.<Void>builder().get();
    }
}
