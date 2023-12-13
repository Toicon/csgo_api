package com.csgo.support;

import com.echo.framework.platform.exception.ApiException;

/**
 * @author admin
 */
public class BusinessException extends ApiException {

    public BusinessException(ExceptionCode code) {
        super(code.getCode(), code.getMessage());
    }

    public BusinessException(ExceptionCode code, Throwable e) {
        super(code.getCode(), code.getMessage(), e);
    }
}
