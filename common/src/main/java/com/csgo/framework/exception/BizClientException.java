package com.csgo.framework.exception;


/**
 * @author admin
 */
public class BizClientException extends BizException {
    private static final long serialVersionUID = 1008055480804660710L;

    public BizClientException(int code, String message) {
        super(code, message);
    }

    public BizClientException(int code, Throwable cause) {
        super(code, cause);
    }

    public BizClientException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public BizClientException(BizCode bizCode) {
        super(bizCode);
    }

    public BizClientException(BizCode bizCode, Object[] args) {
        super(bizCode, args);
    }

    public static BizClientException of(BizCode bizCode) {
        return new BizClientException(bizCode);
    }

    public static BizClientException of(BizCode bizCode, Object[] args) {
        return new BizClientException(bizCode, args);
    }


}
