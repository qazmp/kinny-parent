package com.kinny.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.kinny.common.exception.CacheException;
import com.kinny.mapper.tbSeckillGoodsMapper;
import com.kinny.mapper.tbSeckillOrderMapper;
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
    private tbSeckillOrderMapper seckillOrderMapper;

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
        this.redisTemplate.boundHashOps("seckillOrderList").put(principal, seckillOrder);
    }

    @Override
    public void updateSeckillOrderDuarbilityCacheToDatabase(String principal, String transaction_id) {
        if (principal == null) {
            throw new IllegalArgumentException("认证的用户名称为空");
        }
        tbSeckillOrder seckillOrder = (tbSeckillOrder) this.redisTemplate.boundHashOps("seckillOrderList").get(principal);
        if (seckillOrder == null) {
            throw new CacheException("没有该用户订单");
        }
        if(!"0".equals(seckillOrder.getStatus())) {
            throw new CacheException("该用户秒杀订单状态异常");
        }
        seckillOrder.setStatus("1"); // 完成了付款
        seckillOrder.setPayTime(new Date()); // 支付的时间
        seckillOrder.setTransactionId(transaction_id); // 交易平台交易流水号

        // 持久化
        this.seckillOrderMapper.insert(seckillOrder);

        // 删除缓存中
        this.redisTemplate.boundHashOps("seckillOrderList").delete(principal);
    }

    @Override
    public void deleteOrderAndResetStock(String principal, String id) {
        // 1 获取缓存中的订单
        tbSeckillOrder seckillOrder = (tbSeckillOrder) this.redisTemplate.boundHashOps("seckillOrderList").get(principal);

        if (seckillOrder != null) {
            // 2 删除缓存中的订单
            this.redisTemplate.boundHashOps("seckillOrderList").delete(principal);

            // 3 恢复秒杀商品库存
            tbSeckillGoods seckillGoods = (tbSeckillGoods) this.redisTemplate.boundHashOps("seckillGoodsList").get(seckillOrder.getSeckillId() + "");
            if (seckillGoods == null) { // 商品库存是最后一件超时
                seckillGoods = this.seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId()); // 将数据库中的
                seckillGoods.setStockCount(1); // 库存量1
                this.redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId() + "", seckillGoods);
            }else {
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1); // 恢复库存
                this.redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId() + "", seckillGoods);
            }
            System.out.println("订单超时，恢复秒杀商品的库存。。。。。。。。。。。。。。。");
        }


    }
}
