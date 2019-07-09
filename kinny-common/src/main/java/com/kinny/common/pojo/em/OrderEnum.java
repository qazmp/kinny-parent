package com.kinny.common.pojo.em;

/**
 * @author qgy
 * @create 2019/7/9 - 11:42
 */
public enum OrderEnum {

    NOT_PATMENT("1", "未支付"),
    PATMENT("2", "已支付"),
    NOT_SEND("3", "未发货"),
    SEND("4", "已发货"),
    FINISH("5", "已完成"),
    CLOSE("6", "已关闭"),
    PRE_COMMENT("7", "待评价");

    private String code;

    private String message;

    public String getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

    OrderEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
