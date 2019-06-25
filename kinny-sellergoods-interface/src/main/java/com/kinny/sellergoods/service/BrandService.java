package com.kinny.sellergoods.service;

import com.kinny.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/2 - 16:05
 */
public interface BrandService {

    /**
     *  查询全部品牌数据
     * @return
     */
    public List<TbBrand> findAll();

    /**
     *  根据当前页数 以及 一页条数分页查询 使用map 易于扩展参数 不用修改接口
     * @param map
     * @return
     */
    public Map<String, Object> findPage(Map<String, Object> map);

    /**
     *  根据id查询品牌数据
     * @param id
     * @return
     */
    public TbBrand findOne(String id);


    /**
     *  保存品牌 新增或者修改 根据是否含有记录唯一标识
     * @param brand
     */
    public void save(TbBrand brand);

    /**
     *  根据主键集合批量删除品牌
     * @param ids
     */
    public void batchDelete(List<String> ids);

}
