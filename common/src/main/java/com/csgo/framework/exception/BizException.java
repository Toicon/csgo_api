package com.csgo.framework.exception;

import com.csgo.framework.constant.ErrorConstant;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1008055480804660710L;

    private final Integer code;
    private BizCode bizCode = null;

    private Object[] args;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BizException(BizCode bizCode) {
        super(bizCode.getMessage());
        this.bizCode = bizCode;
        this.code = bizCode.getCode();
    }

    public BizException(BizCode bizCode, Object[] args) {
        super(bizCode.getMessage());
        this.bizCode = bizCode;
        this.code = bizCode.getCode();
        this.args = args;
    }

    public static BizException of(String msg) {
        return new BizException(ErrorConstant.SERVER_ERROR, msg);
    }

    public static BizException of(int code, String msg) {
        return new BizException(code, msg);
    }

    public static BizException of(BizCode bizCode) {
        return new BizException(bizCode);
    }

    public static BizException of(BizCode bizCode, String msg) {
        return new BizException(bizCode.getCode(), msg);
    }

}
