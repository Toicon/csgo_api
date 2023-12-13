package com.csgo.web.controller.blindbox;

import com.csgo.domain.plus.blind.BlindBoxTypePlus;
import com.csgo.service.blind.BlindBoxTypeService;
import com.csgo.support.StandardExceptionCode;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.blindbox.UpdateBlindBoxTypeRequest;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Api
@RequestMapping("/blindBox/type")
public class AdminBlindBoxTypeV2Controller extends BackOfficeController {

    @Autowired
    private BlindBoxTypeService blindBoxTypeService;


    @PutMapping
    public BaseResponse<Void> update(@Valid @RequestBody UpdateBlindBoxTypeRequest request) {
        if (StringUtils.isEmpty(request.getName())) {
            throw new ApiException(StandardExceptionCode.BLIND_BOX_TYPE_NAME_NOT_EXISTS, "名称不能为空");
        }
        BlindBoxTypePlus type = blindBoxTypeService.get(request.getId());
        BeanUtils.copyProperties(request, type);
        blindBoxTypeService.update(type);
        return BaseResponse.<Void>builder().get();
    }

}
