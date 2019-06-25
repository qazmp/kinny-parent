package com.kinny.sellergoods.service;

import com.kinny.pojo.TbSpecification;
import com.kinny.pojo.group.SpecificationGroup;

import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/6 - 9:13
 */
public interface SpecificationService {

    /**
     *  查询所有的规格 不包含规格选项
     * @return
     */
    public List<TbSpecification> findAll();

    /**
     *  高级查询分页 参数定义为map 参数易扩展 无需修改接口
     * @param paramMap
     * @return
     */
    public Map<String, Object> findPage(Map<String, Object> paramMap);

    /**
     *  根据id获取规格以及规格选项
     * @param id
     * @return
     */
    public SpecificationGroup findOne(String id);

    /**
     *  新增规格 以及 规格选项
     * @param specificationGroup
     */
    public void cascadeAdd(SpecificationGroup specificationGroup);

    /**
     *  修改规格以及规格选项
     * @param specificationGroup
     */
    public void cascadeUpdate(SpecificationGroup specificationGroup);

    /**
     *  规格批量删除
     * @param ids
     */
    public void batchRemove(String [] ids);


}
