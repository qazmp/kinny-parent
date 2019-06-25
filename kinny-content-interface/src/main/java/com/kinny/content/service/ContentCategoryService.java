package com.kinny.content.service;

import com.kinny.pojo.TbContentCategory;

import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/25 - 9:31
 */
public interface ContentCategoryService {


    /**
     *  查询全部数据 级联新增 下拉框复选框数据
     * @return
     */
    public List<TbContentCategory> findAll();

    /**
     *  分页查询商品分类
     * @param map 参数易扩展 提高接口层代码可复用性
     * @return
     */
    public Map<String, Object> findPage(Map<String, Object> map);

    /**
     *  查询单条广告分类 用于修改时 记录回显
     * @param id
     * @return
     */
    public TbContentCategory findOne(String id);

    /**
     *  保存广告 新增或者修改 记录主键是否存在
     * @param contentCategory
     */
    public void save(TbContentCategory contentCategory);


    /**
     *  批量删除广告记录
     * @param ids
     */
    public void batchDelete(String[] ids);


}
