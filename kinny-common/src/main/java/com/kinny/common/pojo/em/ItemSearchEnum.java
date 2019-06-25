package com.kinny.common.pojo.em;

/**
 * @author qgy
 * @create 2019/6/10 - 10:57
 */
public enum ItemSearchEnum {

    PARAMETER_KYEWORDS("keywords", "sku检索的关键字， map集合的key"),
    RESULT_LIST("rows", "检索的sku数据 返回map的key");

    private String code;

    private String message;

    public String getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

    ItemSearchEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
