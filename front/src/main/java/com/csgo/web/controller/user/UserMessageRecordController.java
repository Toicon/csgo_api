package com.csgo.web.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.SearchUserMessageRecordCondition;
import com.csgo.domain.plus.message.UserMessageItemRecordDTO;
import com.csgo.domain.plus.message.UserMessageRecord;
import com.csgo.service.UserMessageItemRecordService;
import com.csgo.service.UserMessageRecordService;
import com.csgo.support.DataConverter;
import com.csgo.web.request.user.SearchUserMessageRecordRequest;
import com.csgo.web.response.user.UserMessageProductRecordResponse;
import com.csgo.web.response.user.UserMessageRecordResponse;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "背包流水")
@RestController
@RequestMapping("/user/message/record")
@Slf4j
public class UserMessageRecordController {

    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private UserMessageItemRecordService userMessageItemRecordService;

    /**
     * 查询所有对应的背包流水
     *
     * @return
     */
    @ApiOperation("查询所有对应的背包流水")
    @PostMapping(value = "pagination")
    public PageResponse<UserMessageRecordResponse> pagination(@Valid @RequestBody SearchUserMessageRecordRequest request) {
        SearchUserMessageRecordCondition condition = DataConverter.to(SearchUserMessageRecordCondition.class, request);
        Page<UserMessageRecord> pagination = userMessageRecordService.pagination(condition);
        List<Integer> userMessageRecordIds = pagination.getRecords().stream().map(UserMessageRecord::getId).collect(Collectors.toList());
        Map<Integer, List<UserMessageItemRecordDTO>> recordListMap = userMessageItemRecordService.findWithProductByRecordIds(userMessageRecordIds).stream().collect(Collectors.groupingBy(UserMessageItemRecordDTO::getRecordId));
        return DataConverter.to(record -> {
            UserMessageRecordResponse response = new UserMessageRecordResponse();
            BeanUtils.copyProperties(record, response);
            if (recordListMap.containsKey(record.getId())) {
                response.setProductList(recordListMap.get(record.getId()).stream().map(userMessageItemRecord -> {
                    UserMessageProductRecordResponse recordResponse = new UserMessageProductRecordResponse();
                    recordResponse.setImg(userMessageItemRecord.getImg());
                    recordResponse.setGiftGradeG(String.valueOf(userMessageItemRecord.getOutProbability()));
                    return recordResponse;
                }).collect(Collectors.toList()));
            }
            return response;
        }, pagination);
    }

}
