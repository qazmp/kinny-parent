package com.kinny.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.kinny.common.exception.CacheException;
import com.kinny.mapper.tbSeckillGoodsMapper;
import com.kinny.pojo.tbSeckillGoods;
import com.kinny.pojo.tbSeckillOrder;
import com.kinny.seckill.service.SeckillOrderService;
import com.kinny.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author qgy
 * @create 2019/7/17 - 11:10
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private tbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;


    @Override
    public void submitOrder(String principal, String id) {

        if (principal == null || id == null) {
            throw new IllegalArgumentException("用户姓名或者商品主键不能为空");
        }

        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("商品主键必须是数字");
        }
        // 获取缓存中的秒杀商品
        tbSeckillGoods seckillGoods = (tbSeckillGoods) this.redisTemplate.boundHashOps("seckillGoodsList").get(id);
        if (seckillGoods == null) {
            throw new CacheException("没有该秒杀商品");
        }

        if (seckillGoods.getStockCount() <= 0) {
            throw new CacheException("该秒杀商品已抢购一空");
        }

        // 更新商品库存量
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        this.redisTemplate.boundHashOps("seckillGoodsList").put(id, seckillGoods);
        if(seckillGoods.getStockCount() <= 0) { // 商品抢购完成 持久化到数据库
            System.out.println("商品抢购完成 持久化到数据库 [mysql]");
            this.seckillGoodsMapper.updateByPrimaryKey(seckillGoods); // 可以log
            this.redisTemplate.boundHashOps("seckillGoodsList").delete(id);
        }

        // 创建并缓存秒杀订单对象
        tbSeckillOrder seckillOrder = new tbSeckillOrder();
        seckillOrder.setId(this.idWorker.nextId()); // 秒杀订单主键 推特雪花算法
        seckillOrder.setSeckillId(Long.parseLong(id)); // 秒杀商品id
        seckillOrder.setSellerId(seckillGoods.getSellerId()); // 商品商家
        seckillOrder.setUserId(principal); // 购买用户姓名
        seckillOrder.setMoney(seckillGoods.getCastPrice()); // 商品秒杀价 购买1
        seckillOrder.setCreateTime(new Date()); // 创建
        seckillOrder.setStatus("0"); // 未支付订单
        System.out.println("创建并缓存秒杀订单对象 [redis]");
        this.redisTemplate.boundHashOps("seckillOrderList").put(principal, seckillGoods);
    }
}
