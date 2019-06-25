package com.kinny.sellergoods.service;

import com.kinny.pojo.TbSeller;

import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/8 - 10:42
 */
public interface SellerService {

    /**
     *  保存商家信息 新增（入住）
     * @param seller
     */
    public void add(TbSeller seller);

    /**
     *  分页查询对于数据多
     * @param map
     * @return
     */
    public Map<String, Object> findPage(Map<String, Object> map);

    /**
     *  根据主键查询
     * @param sellerId
     * @return
     */
    public TbSeller findOne(String sellerId);

    /**
     *  跟新商家状态
     * @param sellerId
     * @param status
     */
    public void updateStatus(String sellerId, String status);


}
