package com.kinny.common.pojo.em;

/**
 * @author qgy
 * @create 2019/6/11 - 15:07
 */
public enum CacheEnum {

    CACHE_CATEGORY_ID("templateId", "hash key 分类名称 value 类型模板主键"),
    CACHE_ID_BRAND("brandList", "hash key 模板主键 value 品牌集合"),
    CACHE_ID_SPECIFICATION("specificationList", "hash key 模板主键 value 规格及规格选项集合"),
    CART_REMOTE_HASH("cartList", "远程购物车列表hash");


    public final String code;
    public final String message;

    CacheEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
