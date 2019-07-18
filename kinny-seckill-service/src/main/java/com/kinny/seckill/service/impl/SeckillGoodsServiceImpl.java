package com.kinny.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.kinny.mapper.tbSeckillGoodsMapper;
import com.kinny.pojo.tbSeckillGoods;
import com.kinny.pojo.tbSeckillGoodsExample;
import com.kinny.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * @author qgy
 * @create 2019/7/16 - 11:30
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private tbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     *  缓存的数据结构
     *   entry key 商品主键
     *          value 商品
     * @return
     */
    @Override
    public List<tbSeckillGoods> findAllFromRedis() {
        // 判断缓存中是否含有
        List<tbSeckillGoods> list = null;
        try {
            list = this.redisTemplate.boundHashOps("seckillGoodsList").values();
        } catch (Exception e) {
            System.out.println("警告 redis 出现异常 请及时修复 否则高频发可能会压垮数据库 " + e.getMessage());
        }
        if(list == null || list.size() <= 0) {
            tbSeckillGoodsExample example = new tbSeckillGoodsExample();
            tbSeckillGoodsExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1"); // 运行商审核通过
            criteria.andStartTimeLessThanOrEqualTo(new Date()); // 开始时间小于当前时间
            criteria.andEndTimeGreaterThanOrEqualTo(new Date()); // 结束时间大于等于当前时间
            criteria.andStockCountGreaterThan(0); // 剩余库存大于0
            list = this.seckillGoodsMapper.selectByExample(example);
            // 放入缓存
            try {
                for (tbSeckillGoods seckillGoods : list) {
                    this.redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId() + "" , seckillGoods);
                }
                System.out.println("从数据库读取数据 并放入缓存");
            } catch (Exception e) {
                System.out.println("警告 redis 出现异常 请及时修复 否则高频发可能会压垮数据库 " + e.getMessage());
            }
        }else {
            System.out.println("直接从redis中读取数据 并未访问数据库");
            // todo 缓存中秒杀商品过期
        }


        return list;
    }

    @Override
    public tbSeckillGoods findOneFromRedis(String id) {
        tbSeckillGoods seckillGoods = (tbSeckillGoods) this.redisTemplate.boundHashOps("seckillGoodsList").get(id);
        return seckillGoods;
    }
}
