package com.csgo.web.controller.envelop;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import com.csgo.service.envelop.RedEnvelopSortService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.envelop.SearchRedEnvelopCondition;
import com.csgo.condition.envelop.SearchRedEnvelopRecordCondition;
import com.csgo.domain.plus.envelop.RedEnvelop;
import com.csgo.domain.plus.envelop.RedEnvelopItem;
import com.csgo.domain.plus.envelop.RedEnvelopRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.service.UserService;
import com.csgo.service.envelop.RedEnvelopService;
import com.csgo.support.DataConverter;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.controller.envelop.converter.RedEnvelopConverter;
import com.csgo.web.request.envelop.EditRedEnvelopRequest;
import com.csgo.web.request.envelop.SearchRedEnvelopRecordRequest;
import com.csgo.web.request.envelop.SearchRedEnvelopRequest;
import com.csgo.web.response.envelop.RedEnvelopRecordResponse;
import com.csgo.web.response.envelop.RedEnvelopResponse;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;

import lombok.extern.slf4j.Slf4j;

@Api
@RequestMapping("/red/envelop")
@Slf4j
public class AdminRedEnvelopController extends BackOfficeController {

    @Autowired
    private RedEnvelopService redEnvelopService;
    @Autowired
    private RedEnvelopSortService redEnvelopSortService;

    @Autowired
    private RedEnvelopConverter converter;
    @Autowired
    private UserService userService;

    /**
     * 新增红包
     *
     * @param
     * @return
     */
    @PostMapping
    public BaseResponse<Void> add(@Valid @RequestBody EditRedEnvelopRequest request) {
        redEnvelopService.add(converter.to(request, siteContext.getCurrentUser().getUsername()));
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 编辑红包信息
     *
     * @return
     */
    @PutMapping(value = "/{id}")
    public BaseResponse<Void> update(@PathVariable("id") int id, @Valid @RequestBody EditRedEnvelopRequest request) {
        redEnvelopService.update(converter.to(id, request, siteContext.getCurrentUser().getUsername()));
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping(value = "/{id}/sortId/{sortId}")
    public BaseResponse<Void> updateSortId(@PathVariable("id") int id, @PathVariable("sortId") int sortId) {
        redEnvelopSortService.updateSortId(id, sortId);
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 查询所有对应的红包信息
     *
     * @return
     */
    @PostMapping(value = "/pagination")
    public PageResponse<RedEnvelopResponse> pagination(@Valid @RequestBody SearchRedEnvelopRequest request) {
        SearchRedEnvelopCondition condition = DataConverter.to(SearchRedEnvelopCondition.class, request);
        Page<RedEnvelop> pagination = redEnvelopService.pagination(condition);
        return DataConverter.to(record -> {
            RedEnvelopResponse response = new RedEnvelopResponse();
            BeanUtils.copyProperties(record, response);
            response.setStatus(record.getStatus().name());
            BigDecimal amount = redEnvelopService.getSendAmount(record.getId());
            response.setTotalAmount(amount);
            return response;
        }, pagination);
    }

    /**
     * 根据ID删除对应的红包信息
     *
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public BaseResponse<Void> delete(@PathVariable("id") int id) {
        redEnvelopService.delete(id);
        return BaseResponse.<Void>builder().get();
    }


    /**
     * 查询所有对应的红包信息
     *
     * @return
     */
    @PostMapping(value = "/record")
    public PageResponse<RedEnvelopRecordResponse> queryRecord(@Valid @RequestBody SearchRedEnvelopRecordRequest request) {
        SearchRedEnvelopRecordCondition condition = DataConverter.to(SearchRedEnvelopRecordCondition.class, request);
        if (null != request.getRedEnvelopId()) {
            List<RedEnvelopItem> items = redEnvelopService.findByRedEnvelopId(request.getRedEnvelopId());
            if (!CollectionUtils.isEmpty(items)) {
                condition.setEnvelopItemIds(items.stream().map(RedEnvelopItem::getId).collect(Collectors.toList()));
            }
        }
        Page<RedEnvelopRecord> pagination = redEnvelopService.recordPagination(condition);
        return DataConverter.to(record -> {
            RedEnvelopRecordResponse response = new RedEnvelopRecordResponse();
            BeanUtils.copyProperties(record, response);
            response.setReceiptedDate(record.getCreateDate());
            UserPlus userPlus = userService.getUserPlus(record.getUserId());
            if (userPlus != null) {
                response.setUserName(userPlus.getUserName());
            }
            return response;
        }, pagination);
    }
}
