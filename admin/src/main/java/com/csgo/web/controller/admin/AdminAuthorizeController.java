package com.csgo.web.controller.admin;

import com.csgo.domain.plus.authorize.Authorize;
import com.csgo.service.AuthorizeService;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.response.authorize.AuthorizeResponse;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "权限列表")
@RestController
@RequestMapping("/authorize")
@Slf4j
public class AdminAuthorizeController extends BackOfficeController {

    @Autowired
    private AuthorizeService authorizeService;

    /**
     * 查询所有权限
     *
     * @return
     */
    @GetMapping("queryAll")
    public BaseResponse<List<AuthorizeResponse>> queryAll() {
        return BaseResponse.<List<AuthorizeResponse>>builder().data(authorizeService.query().stream().map(authorize -> to(authorize, authorize.getCode())).collect(Collectors.toList())).get();
    }

    private AuthorizeResponse to(Authorize authorize, String parentCode) {
        AuthorizeResponse response = new AuthorizeResponse();
        BeanUtilsEx.copyProperties(authorize, response);
        if (StringUtils.hasText(parentCode)) {
            List<Authorize> items = authorizeService.findByParent(parentCode);
            if (CollectionUtils.isEmpty(items)) {
                return response;
            }
            response.setItems(items.stream().map(code -> to(code, code.getCode())).collect(Collectors.toList()));
        }
        return response;
    }

}
