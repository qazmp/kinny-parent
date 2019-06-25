package com.kinny.sellergoods.service;

import com.kinny.pojo.TbItemCat;

import java.util.List;

/**
 * @author qgy
 * @create 2019/5/13 - 10:35
 * 商品分类接口层
 */
public interface ItemCatService {

    /**
     *  查询所有分类信息 用于js脚本数据 商品分页列表避免了多次查询后台
     * @return
     */
    public List<TbItemCat> findAll();

    /**
     *  找到指定父分类下的所有子分类 自关联
     * @param parentId
     * @return
     */
    public List<TbItemCat> findByParentId(String parentId);

    /**
     *  根据主键查询分类 用于修改时回显
     * @param id
     * @return
     */
    public TbItemCat findOne(String id);


    /**
     *  保存商品分类 根据是否含有记录主键区别是新增还是修改
     * @param itemCat
     */
    public void save(TbItemCat itemCat);

    public void batchDelete(String [] ids);




}
