package com.kinny.seckill.service;

/**
 * @author qgy
 * @create 2019/7/17 - 11:09
 */
public interface SeckillOrderService {


    /**
     *  秒杀下单
     * @param principal 登录的用户名
     * @param id 秒杀商品id
     */
    public void submitOrder(String principal, String id);


}
