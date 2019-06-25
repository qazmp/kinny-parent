package com.kinny.page.service;

/**
 * @author qgy
 * @create 2019/6/16 - 16:10
 */
public interface ItemPageService {

    /**
     *  利用freemarker 生成指定商品的商品详情页
     *   利用数据脚本存储sku列表 实现类完全的网页静态化
     * @param goodsId spu id
     * @return 页面生成是否
     */
    public boolean genItemPage(String goodsId);

    public boolean deleteItemPage(String [] ids);

}
