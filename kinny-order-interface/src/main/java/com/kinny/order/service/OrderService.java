package com.kinny.order.service;

import com.kinny.pojo.TbOrder;
import com.kinny.pojo.group.CarGroup;

import java.util.List;

/**
 * @author qgy
 * @create 2019/7/9 - 11:11
 */
public interface OrderService {


    /**
     *  添加订单 购物车列表从redis中获取的
     *  注意 控制器要把当前认证用户姓名填充到入参
     * @param order
     */
    public void addOrderAndClear(TbOrder order);


}
