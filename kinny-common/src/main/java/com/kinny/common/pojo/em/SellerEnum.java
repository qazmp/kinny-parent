package com.kinny.common.pojo.em;

/**
 * @author qgy
 * @create 2019/5/8 - 10:50
 */
public enum SellerEnum {
    NOT_CHECK("0", "未审核"),
    CHECKED("1", "已审核"),
    CHECKED_NOT("2", "审核未通过"),
    CLOSE("3", "关闭");

    private String code;

    private String message;

    public String getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

    SellerEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}