package com.kinny.common.pojo.em;

/**
 * @author qgy
 * @create 2019/5/16 - 9:26
 */
public enum GoodsEnum {

    ENABLE_SPECIFICATION("1", "启用商品规格"),
    DISABLE_SPECIFICATION("0", "禁用商品规格"),
    NOT_CHECK("0", "未审核"),
    CHECKED("1", "已审核"),
    CHECKED_NOT("2", "审核未通过"),
    CLOSE("3", "关闭"),
    DEFAULT("1", "默认sku"),
    NOTDEFAULT("0", "不是默认sku"),
    ;
    private String code;

    private String message;

    public String getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

    GoodsEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
