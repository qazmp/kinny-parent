package com.kinny.common.pojo.em;

/**
 * @author qgy
 * @create 2019/5/25 - 10:16
 */
public enum ContentCategoryEnum {


    ENABLE("1", "启用"),
    CACHE_HASH_KEY("content", "redis缓存hash大key"),
    UNENABLE("0", "禁用");

    private String code;

    private String message;

    public String getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

    ContentCategoryEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
