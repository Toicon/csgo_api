package com.csgo.framework.exception;

import cn.hutool.core.util.StrUtil;
import com.csgo.framework.i18n.LocaleMessageSourceResolver;
import com.csgo.framework.util.RUtil;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author admin
 */
@Slf4j
@RestControllerAdvice
public class BizExceptionAdviceHandle {

    @Autowired
    private LocaleMessageSourceResolver localeMessageSourceResolver;

    private String resolveMessage(BizException e) {
        String message = e.getMessage();
        if (e.getBizCode() != null) {
            String temp = localeMessageSourceResolver.getMessage(e.getBizCode().getI18n(), e.getArgs());
            if (StrUtil.isNotBlank(temp)) {
                message = temp;
            }
        }
        return message;
    }

    @ExceptionHandler(BizException.class)
    public BaseResponse<?> bizExceptionHandler(BizException e) {
        String message = resolveMessage(e);
        return RUtil.fail(e.getCode(), null, message);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BizServerException.class)
    public BaseResponse<?> bizServerExceptionHandler(BizServerException e) {
        String message = resolveMessage(e);
        return RUtil.fail(e.getCode(), null, message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BizClientException.class)
    public BaseResponse<?> bizClientExceptionHandler(BizClientException e) {
        String message = resolveMessage(e);
        return RUtil.fail(e.getCode(), null, message);
    }

}
