package com.kinny.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.kinny.common.exception.DbException;
import com.kinny.common.pojo.em.CacheEnum;
import com.kinny.mapper.TbItemCatMapper;
import com.kinny.pojo.TbItemCat;
import com.kinny.pojo.TbItemCatExample;
import com.kinny.sellergoods.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.client.methods.RequestBuilder.put;

/**
 * @author qgy
 * @create 2019/5/13 - 10:37
 */
@Service(timeout = 5000)
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public List<TbItemCat> findAll() {

        List<TbItemCat> itemCats = this.itemCatMapper.selectByExample(null);

        return itemCats;
    }

    @Override
    public List<TbItemCat> findByParentId(String parentIdStr) {

        if (parentIdStr == null) {
            throw new IllegalArgumentException("父分类主键不能为空");
        }

        Long parentId = null;

        try {
            parentId = Long.parseLong(parentIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("父分类主键必须是数字啊");
        }

        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbItemCat> itemCatList = this.itemCatMapper.selectByExample(example);

        cacheTemplateId();

        return itemCatList;
    }

    /**
     *  缓存类型模板id key 分类名称 value 类型模板主键
     *  使用的hash类型 所以不用清空缓存可以通过value覆盖完成缓存与数据库同步
     */
    private void cacheTemplateId() {
        List<TbItemCat> itemCats = this.findAll();
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(CacheEnum.CACHE_CATEGORY_ID.code);
        for (TbItemCat itemCat : itemCats) {
            hashOperations.put(itemCat.getName(), itemCat.getTypeId());
        }
    }

    @Override
    public TbItemCat findOne(String idStr) {

        if (idStr == null) {
            throw new IllegalArgumentException("商品分类主键不能为空");
        }

        Long id = null;

        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("商品分类主键必须是数字");
        }

        TbItemCat tbItemCat = this.itemCatMapper.selectByPrimaryKey(id);

        return tbItemCat;
    }

    @Override
    public void save(TbItemCat itemCat) {

        if (itemCat == null) {
            throw new IllegalArgumentException("商品分类不能为空");
        }

        if (itemCat.getId() == null) {
            this.itemCatMapper.insert(itemCat);
        }else {
            this.itemCatMapper.updateByPrimaryKeySelective(itemCat);
        }

    }

    @Override
    public void batchDelete(String[] ids) {

        // 非空
        if (ids == null) {
            throw new IllegalArgumentException("要删除的商品分类主键数组不能为空啊");
        }

        List<Long> idList = new ArrayList<>();
        // 数字
        for (String id : ids) {
            try {
                idList.add(Long.parseLong(id));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("要删除的商品分类主键必须是数字");
            }
        }

        for (Long id : idList) {
            int i = this.itemCatMapper.deleteByPrimaryKey(id);
            if (i != 1) // 由于与业务逻辑不符而产生的异常 程序无法自动抛出 必须程序员手动抛出自定义异常
                throw new DbException("删除商品分类失败");
        }

    }
}
