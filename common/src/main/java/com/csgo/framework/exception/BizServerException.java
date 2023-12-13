package com.csgo.framework.exception;

import com.csgo.framework.constant.ErrorConstant;

/**
 * @author admin
 */
public class BizServerException extends BizException {
    private static final long serialVersionUID = 1008055480804660710L;

    public BizServerException(int code, String message) {
        super(code, message);
    }

    public BizServerException(int code, Throwable cause) {
        super(code, cause);
    }

    public BizServerException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    private BizServerException(BizCode bizCode) {
        super(bizCode);
    }

    public BizServerException(BizCode bizCode, Object[] args) {
        super(bizCode, args);
    }

    public static BizServerException of(String msg) {
        return new BizServerException(ErrorConstant.SERVER_ERROR, msg);
    }

    public static BizServerException of(BizCode bizCode) {
        return new BizServerException(bizCode);
    }

    public static BizClientException of(BizCode bizCode, Object[] args) {
        return new BizClientException(bizCode, args);
    }

}
