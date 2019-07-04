package com.kinny.cart.service;


import com.kinny.pojo.group.CarGroup;

import java.util.List;

/**
 * 购物车
 * @author qgy
 * @create 2019/7/4 - 11:30
 */
public interface CartService {

    /**
     *  添加商品到购物车列表
     * @param carGroupList 原来的购物车列表
     * @param itemId sku 可以获取商家信息
     * @param num 购买
     * @return 添加后的购物车列表
     */
    public List<CarGroup> addGoodsToCartList(List<CarGroup> carGroupList, Long itemId, Integer num);

}
