package com.csgo.web.controller.operation;

import com.csgo.condition.operation.SearchOperationLogCondition;
import com.csgo.service.opeartion.OperationLogService;
import com.csgo.support.DataConverter;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.operation.SearchOperationLogRequest;
import com.csgo.web.response.operation.OperationLogResponse;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author admin
 */
@Api
@RequestMapping("/operation/log")
public class OperationLogController extends BackOfficeController {

    @Autowired
    private OperationLogService logService;

    @PostMapping("/pagination")
    public PageResponse<OperationLogResponse> luckyProductPagination(@Valid @RequestBody SearchOperationLogRequest request) {
        SearchOperationLogCondition condition = DataConverter.to(SearchOperationLogCondition.class, request);
        return DataConverter.to(record -> {
            OperationLogResponse response = new OperationLogResponse();
            BeanUtils.copyProperties(record, response);
            return response;
        }, logService.pagination(condition));
    }
}
