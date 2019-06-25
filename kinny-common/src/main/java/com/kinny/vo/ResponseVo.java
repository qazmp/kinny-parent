package com.kinny.vo;

/**
 * @author qgy
 * @create 2019/5/4 - 20:11
 */
public class ResponseVo<T> {

    private boolean success;

    private String message;

    private T data;


    public ResponseVo(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public ResponseVo(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseVo(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
