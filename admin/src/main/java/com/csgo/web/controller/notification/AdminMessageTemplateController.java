package com.csgo.web.controller.notification;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.notification.SearchMessageTemplateCondition;
import com.csgo.domain.plus.message.MessageTemplate;
import com.csgo.service.MessageTemplateService;
import com.csgo.support.DataConverter;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.notification.EditMessageTemplateRequest;
import com.csgo.web.request.notification.SearchMessageTemplateRequest;
import com.csgo.web.response.notification.MessageTemplateResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "消息模板管理")
@RestController
@RequestMapping("/message/template")
@Slf4j
public class AdminMessageTemplateController extends BackOfficeController {

    @Autowired
    private MessageTemplateService messageTemplateService;


    /**
     * 新增消息模板
     *
     * @param
     * @return
     */
    @ApiOperation("新增消息模板（后台管理）")
    @PostMapping(value = "add")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型"),
            @ApiImplicitParam(name = "title", value = "标题"),
            @ApiImplicitParam(name = "content", value = "内容")
    })
    @Log(desc = "新增消息模板")
    public Result add(@Valid @RequestBody EditMessageTemplateRequest request) {
        if (messageTemplateService.selectOne(request.getType()) != null) {
            return new Result().fairResult("模板已经存在");
        }
        int num = messageTemplateService.add(request.getType(), request.getTitle(), request.getContent());
        return new Result().result(num);
    }

    /**
     * 编辑消息模板信息
     *
     * @return
     */
    @ApiOperation("编辑消息模板信息（后台管理）")
    @PutMapping(value = "update/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id"),
            @ApiImplicitParam(name = "type", value = "类型"),
            @ApiImplicitParam(name = "title", value = "标题"),
            @ApiImplicitParam(name = "content", value = "内容")
    })
    @Log(desc = "修改消息模板")
    public Result update(@PathVariable("id") int id, @Valid @RequestBody EditMessageTemplateRequest request) {
        MessageTemplate template = messageTemplateService.selectOne(request.getType());
        if (template != null && template.getId() != id) {
            return new Result().fairResult("模板已经存在");
        }
        int num = messageTemplateService.update(id, request.getType(), request.getTitle(), request.getContent());
        return new Result().result(num);
    }

    /**
     * 查询所有对应的消息模板信息
     *
     * @return
     */
    @ApiOperation("查询所有对应的消息模板信息（后台管理）")
    @PostMapping(value = "queryAll")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据")
    })
    @Log(desc = "查询消息模板列表")
    public PageResponse<MessageTemplateResponse> queryAll(@Valid @RequestBody SearchMessageTemplateRequest request) {
        SearchMessageTemplateCondition condition = DataConverter.to(SearchMessageTemplateCondition.class, request);
        Page<MessageTemplate> pagination = messageTemplateService.pagination(condition);
        return DataConverter.to(record -> {
            MessageTemplateResponse response = new MessageTemplateResponse();
            BeanUtils.copyProperties(record, response);
            response.setType(record.getType().name());
            return response;
        }, pagination);
    }

    /**
     * 根据ID删除对应的消息模板信息
     *
     * @return
     */
    @ApiOperation("根据ID删除对应的消息模板信息（后台管理）")
    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "消息模板id")
    })
    @Log(desc = "删除消息模板")
    public Result delete(@PathVariable("id") int id) {
        int num = messageTemplateService.delete(id);
        return new Result().result(num);
    }


}
