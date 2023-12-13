package com.csgo.web.controller.blindbox;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.blind.SearchBlindBoxProductCondition;
import com.csgo.domain.plus.blind.BlindBoxProductDTO;
import com.csgo.service.blind.BlindBoxProductService;
import com.csgo.support.DataConverter;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.blindbox.SearchBlindBoxProductRequest;
import com.csgo.web.response.blindbox.BlindBoxProductResponse;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Api
@RequestMapping("/blindBoxPro/product")
public class AdminBlindBoxProductV2Controller extends BackOfficeController {

    @Autowired
    private BlindBoxProductService blindBoxProductService;

    @PostMapping("/pagination")
    public PageResponse<BlindBoxProductResponse> pagination(@Valid @RequestBody SearchBlindBoxProductRequest request) {
        SearchBlindBoxProductCondition condition = DataConverter.to(SearchBlindBoxProductCondition.class, request);
        Page<BlindBoxProductDTO> pagination = blindBoxProductService.pagination(condition);
        return DataConverter.to(product -> {
            BlindBoxProductResponse response = new BlindBoxProductResponse();
            BeanUtils.copyProperties(product, response);
            return response;
        }, pagination);
    }

}
