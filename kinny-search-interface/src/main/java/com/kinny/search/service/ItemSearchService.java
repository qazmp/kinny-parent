package com.kinny.search.service;

import com.kinny.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/6/10 - 10:18
 */
public interface ItemSearchService {


    /**
     *  检索数据
     *  参数返回值类型为Map
     *  参数易扩展 当需要增加传递过来的实参时 只需要增加map元素即可 而无需修改参数列表
     *  提高了业务层接口代码的可复用性
     * @param parameterMap
     * @return
     */
    public Map<String, Object> search(Map<String, Object> parameterMap);

    /**
     *  索引库与数据库记录同步
     * @param items sku列表记录
     */
    public void batchImportSku(List<TbItem> items);

    /**
     * 保持 索引库数据与数据库记录保持同步
     * @param id spu主键
     */
    public void deleteBySpuId(String id);

}
