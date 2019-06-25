package com.kinny.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.common.exception.DbException;
import com.kinny.pojo.TbGoods;
import com.kinny.pojo.TbSeller;
import com.kinny.pojo.group.GoodsGroup;
import com.kinny.sellergoods.service.GoodsService;
import com.kinny.vo.ResponseVo;
import com.sun.org.apache.regexp.internal.recompile;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/15 - 8:47
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;



    @RequestMapping("/findPage")
    public ResponseVo<Map<String, Object>> findPage(@RequestBody(required = true) TbGoods goods,
                                                    @RequestParam(required = false, defaultValue = "1") String pageIndex,
                                                    @RequestParam(required = false, defaultValue = "10") String pageSize) {

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("pageIndex", pageIndex);
        objectMap.put("pageSize", pageSize);
        objectMap.put("goods", goods);
        // 金查询当前商家
        goods.setSellerId(((TbSeller)SecurityUtils.getSubject().getPrincipal()).getSellerId());

        Map<String, Object> page = null;

        try {
            page = this.goodsService.findPage(objectMap);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false , e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "商品查询失败");
        }

        return new ResponseVo<>(true, page);
    }

    @RequestMapping("/findOne")
    public ResponseVo<GoodsGroup> findOne(@RequestParam(required = true) String id) {

        try {
            GoodsGroup goodsGroup = this.goodsService.findOne(id);
            return new ResponseVo<>(true, goodsGroup);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false , e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "商品查询失败");
        }

    }


    /**
     *  商品信息录入
     * @param goodsGroup
     * @return
     */
    @RequestMapping("/save")
    public ResponseVo save(@RequestBody(required = true) GoodsGroup goodsGroup) {
        System.out.println("goodsGroup = [" + goodsGroup + "]");
        // todo jsr303
        try {

            TbSeller principal = (TbSeller) SecurityUtils.getSubject().getPrincipal();
            goodsGroup.getGoods().setSellerId(principal.getSellerId());
            this.goodsService.save(goodsGroup);
            return new ResponseVo(true, "商品信息保存成功");
        } catch (IllegalArgumentException e) {
            return new ResponseVo(false, e.getMessage());
        } catch (DbException e) {
            return new ResponseVo(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "商品信息保存失败");
        }

    }


}
