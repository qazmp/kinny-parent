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
     *  创建支付日志 缓存
     *  注意 控制器要把当前认证用户姓名填充到入参
     * @param order
     */
    public void addOrderAndClear(TbOrder order);

    /**
     *  买家付款后 跟新支付日志 订单 清空缓存
     * @param out_trade_no 支付订单号
     * @param trade_no 支付宝交易凭证号
     */
    public void paySuccessUpdatePayLogAndOrder(String out_trade_no, String trade_no);


}
