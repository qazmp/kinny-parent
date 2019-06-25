package com.kinny.content.service;

import com.kinny.pojo.TbContent;

import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/25 - 9:30
 */
public interface ContentService {

    /**
     *  分页查询 数据量较大不能一页展示
     * @param map 参数易扩展 当需要添加实参 只需要添加map实参元素
     * @return
     */
    public Map<String, Object> findPage(Map<String, Object> map);

    /**
     *  根据主键查询 通常用于修改之前回显记录信息
     * @param id
     * @return
     */
    public TbContent findOne(String id);

    /**
     *  保存广告信息
     * @param content
     */
    public void save(TbContent content);


    public void delete(String [] ids);


    /**前台**/

    /**
     *  根据广告类型查询 注意广告排序
     * @return
     */
    public List<TbContent> findByCategoryId(String categoryId);






}
