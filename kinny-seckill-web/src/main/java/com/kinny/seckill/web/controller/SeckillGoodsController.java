package com.kinny.seckill.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.pojo.tbSeckillGoods;
import com.kinny.seckill.service.SeckillGoodsService;
import com.kinny.vo.ResponseVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qgy
 * @create 2019/7/16 - 15:25
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {


    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     *  查询商品秒杀列表
     * @return
     */
    @RequestMapping("/findGoodsList")
    public ResponseVo<List<tbSeckillGoods>> findGoodsList() {

        List<tbSeckillGoods> goodsList = null;
        try {
            goodsList = this.seckillGoodsService.findAllFromRedis();
        } catch (Exception e) {
            return new ResponseVo<>(false, "获取商品列表失败");
        }

        return new ResponseVo<>(true, goodsList);
    }

    @RequestMapping("/findOne")
    public ResponseVo<tbSeckillGoods> goToSeckillDetail(@RequestParam(required = true) String id) {
        try {
            tbSeckillGoods seckillGoods = this.seckillGoodsService.findOneFromRedis(id);
            return new ResponseVo<>(true, seckillGoods);
        } catch (Exception e) {
            return new ResponseVo<>(false, "查询商品失败");
        }
    }

}
