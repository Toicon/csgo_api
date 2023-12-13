package com.csgo.framework.util;

import com.csgo.framework.constant.ErrorConstant;
import com.echo.framework.platform.web.request.PageRequest;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import lombok.experimental.UtilityClass;

/**
 * @author admin
 */
@UtilityClass
public class RUtil {

    private static final Integer FAIL_CODE = ErrorConstant.SERVER_ERROR;
    private static final String FAIL_MSG = ErrorConstant.FAIL_MSG;

    public static <T> BaseResponse<T> ok() {
        return new BaseResponse<>();
    }

    public static <T> BaseResponse<T> ok(T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setData(data);
        return response;
    }

    public static <T> BaseResponse<T> ok(T data, String msg) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setData(data);
        response.setMessage(msg);
        return response;
    }

    public static <T> BaseResponse<T> fail() {
        return result(FAIL_CODE, FAIL_MSG, null);
    }

    public static <T> BaseResponse<T> fail(String msg) {
        return result(FAIL_CODE, msg, null);
    }

    public static <T> BaseResponse<T> fail(T data) {
        return result(FAIL_CODE, FAIL_MSG, data);
    }

    public static <T> BaseResponse<T> fail(T data, String msg) {
        return result(FAIL_CODE, msg, data);
    }

    public static <T> BaseResponse<T> fail(int code, T data, String msg) {
        return result(code, msg, data);
    }

    public static <T> BaseResponse<T> result(int code, String msg, T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setCode(code);
        response.setMessage(msg);
        response.setData(data);
        return response;
    }

    public static <T> PageResponse<T> emptyPage(PageRequest request) {
        PageResponse<T> response = new PageResponse<>();
        PageResponse.PageResult<T> data = new PageResponse.PageResult<>();
        data.setPageIndex(request.getPageIndex());
        data.setPageSize(request.getPageSize());
        data.setTotal(0);

        response.setData(data);
        return response;
    }

}
