package com.csgo.web.controller.i18n;

import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.i18n.LocaleMessageSourceResolver;
import com.csgo.framework.util.RUtil;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author admin
 */
@Slf4j
@Api
@RequestMapping("/i18n")
@RequiredArgsConstructor
public class FrontI18nController {

    private final LocaleMessageSourceResolver localeMessageSourceResolver;

    @RequestMapping("/hello")
    public BaseResponse<String> hello(@RequestParam(defaultValue = "welcome") String code) {
        String welcome = localeMessageSourceResolver.getMessage(code);
        log.info("message:{}", welcome);
        return RUtil.ok(welcome);
    }

    @RequestMapping("/e")
    public BaseResponse<String> exception() {
        throw BizClientException.of(CommonBizCode.RED_LIMIT_AMOUNT_NO_ACHIEVE, new Object[]{"20"});
    }

}
