package com.csgo.web.controller.gift;

import com.csgo.domain.plus.gift.GiftType;
import com.csgo.service.GiftTypeService;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.gift.EditGiftTypeRequest;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Api
@RequestMapping("/gifttype")
@Slf4j
public class AdminGiftTypeServiceV2Controller extends BackOfficeController {

    @Autowired
    private GiftTypeService giftTypeService;

    /**
     * 新增礼包类型
     *
     * @param
     * @return
     */
    @PostMapping
    public BaseResponse<Void> add(@Valid @RequestBody EditGiftTypeRequest request) {
        GiftType giftType = new GiftType();
        BeanUtilsEx.copyProperties(request, giftType);
        giftTypeService.add(giftType);
        return BaseResponse.<Void>builder().get();
    }

}
