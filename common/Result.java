package com.factory.common;

import lombok.Data;

@Data
public class Result<T> {
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;

    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data, String msg) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS_CODE);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(T data) {
        return success(data, "操作成功");
    }

    public static <T> Result<T> success() {
        return success(null, "操作成功");
    }

    public static <T> Result<T> error(String msg) {
        return error(ERROR_CODE, msg);
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}