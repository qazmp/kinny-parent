package com.kinny.sellergoods.service;

import com.kinny.pojo.TbItem;
import com.kinny.pojo.group.GoodsGroup;

import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/15 - 8:45
 */
public interface GoodsService {

    /**
     *  分页查询商品信息 map 参数易扩展 增加参数时无需修改接口层代码提高接口的可复用性
     * @param map
     * @return
     */
    public Map<String, Object> findPage(Map<String, Object> map);


    /**
     *  查询sku列表数据根据spu主键以及商品状态
     * @param id
     * @param status
     * @return
     */
    public List<TbItem> findSkuBySpuIdAndStatus(String id, String status);

    public GoodsGroup findOne(String id);


    /**
     *  新增或者修改商品
     * @param goodsGroup
     */
    public void save(GoodsGroup goodsGroup);

    /**
     *  修改商品的状态
     * @param id
     * @param s
     */
    public void updateStatus(String id, String s);

    public void batchDelete(String [] idS);




}
