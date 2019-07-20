package com.kinny.task;

import com.kinny.mapper.tbSeckillGoodsMapper;
import com.kinny.pojo.tbSeckillGoods;
import com.kinny.pojo.tbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author qgy
 * @create 2019/7/19 - 17:17
 */
@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private tbSeckillGoodsMapper seckillGoodsMapper;

    /**
     *  查询数据库数据 放入到缓存中 不是全量 会导致信息覆盖 （商品的库存）
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void incrementUpdateSeckillGoods() {
        // 1 获取缓存中所有的秒杀商品主键
        List<Long> keys = new ArrayList<>(this.redisTemplate.boundHashOps("seckillGoodsList").keys());
        System.out.println("缓存中存在的秒杀商品主键列表 " + keys);
        // 2 获取数据库中 秒杀时间库存满足的秒杀商品
        tbSeckillGoodsExample example = new tbSeckillGoodsExample();
        tbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStockCountGreaterThan(0);
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());
        criteria.andStatusEqualTo("1");
        if(keys.size() != 0) {
            criteria.andIdNotIn(keys); // 缓存不存在的
        }
        List<tbSeckillGoods> seckillGoods = this.seckillGoodsMapper.selectByExample(example);
        // 3 放入

        for (tbSeckillGoods seckillGood : seckillGoods) {
            System.out.println("增量更新的秒杀商品主键 " + seckillGood.getId());
            this.redisTemplate.boundHashOps("seckillGoodsList").put(seckillGood.getId().toString(), seckillGood);
        }

    }

    /**
     *  查询缓存中 时间结束的或者库存不足的 持久化到MySQL
     */
    @Scheduled(cron = "* * * * * ?")
    public void clearCacheToDatebase() {
        System.out.println(new Date());
        // 获取秒杀商品
        List<tbSeckillGoods> seckillGoodsList = this.redisTemplate.boundHashOps("seckillGoodsList").values();
        System.out.println("seckillGoodsList = " + seckillGoodsList);
        for (tbSeckillGoods seckillGoods : seckillGoodsList) {
            if(seckillGoods.getEndTime().getTime() < new Date().getTime() || seckillGoods.getStockCount() <= 0) {
                // 删除缓存中的
                this.redisTemplate.boundHashOps("seckillGoodsList").delete(seckillGoods.getId() + "");
                // 保存到
                this.seckillGoodsMapper.updateByPrimaryKey(seckillGoods);

                System.out.println("下架的商品id " + seckillGoods.getId());
            }
        }


    }



}
