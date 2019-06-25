package com.kinny.sellergoods.service;

import com.kinny.pojo.TbTypeTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/7 - 9:46
 */
public interface TypeTemplateService {


    /**
     *  查询全部类型模板
     * @return
     */
    public List<TbTypeTemplate> findAll();

    /**
     *  分页
     * @param param
     * @return
     */
    public Map<String, Object> findPage(Map<String, Object> param);

    /**
     *  查询单条模板记录
     * @param id
     * @return
     */
    public TbTypeTemplate findOne(String id);

    /**
     *  查询单条 包含规格下的选项
     * @param id
     * @return
     */
    public TbTypeTemplate findOneContainsSpecOptions(String id);


    /**
     *  保存类型模板
     * @param typeTemplate
     */
    public void save(TbTypeTemplate typeTemplate);

    /**
     *  批量删除类型模板记录
     * @param ids
     */
    public void batchDelete(String [] ids);


}
