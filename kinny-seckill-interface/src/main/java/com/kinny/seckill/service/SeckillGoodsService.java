package com.kinny.seckill.service;

import com.kinny.pojo.tbSeckillGoods;

import java.util.List;

/**
 * @author qgy
 * @create 2019/7/16 - 11:29
 */
public interface SeckillGoodsService {

    /**
     *  查询所有在秒杀时间段 库存大于0 已审核的商品
     *  数据量较小
     * @return
     */
    public List<tbSeckillGoods> findAllFromRedis();

    /**
     *  根据主键查询 秒杀详情页
     * @param id
     * @return
     */
    public tbSeckillGoods findOneFromRedis(String id);


}
