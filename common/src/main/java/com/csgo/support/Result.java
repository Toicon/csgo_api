package com.csgo.support;

import lombok.Data;

/**
 * 返回给前端的控制信息
 */
@Data
public class Result {

    private static final int SUCCESS = 200;
    private static final int FAILED = 500;
    private int code;
    private String message;
    private Object data;

    public Result() {
    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public Result result(Object data) {
        return new Result(SUCCESS, "success", data);
    }

    public Result fairResult(String data) {
        return new Result(FAILED, data, null);
    }


}
