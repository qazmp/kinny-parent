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

    /**
     *  更新redis中的订单状态 并持久化到数据库
     * @param principal
     * @param transaction_id
     */
    public void updateSeckillOrderDuarbilityCacheToDatabase(String principal, String transaction_id);

    /**
     *  订单超时删除订单并恢复库存
     * @param principal
     * @param id
     */
    public void deleteOrderAndResetStock(String principal, String id);


}
