package com.kinny.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kinny.common.exception.DbException;
import com.kinny.common.pojo.em.ContentCategoryEnum;
import com.kinny.content.service.ContentService;
import com.kinny.mapper.TbContentMapper;
import com.kinny.pojo.TbContent;
import com.kinny.pojo.TbContentCategory;
import com.kinny.pojo.TbContentExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/25 - 9:37
 */
@Service
public class ContentServiceImpl implements ContentService {


    @Autowired
    private TbContentMapper contentMapper;



    @Override
    public Map<String, Object> findPage(Map<String, Object> map) {


        String pageIndexS = (String) map.get("pageIndex");
        String pageSizeS = (String) map.get("pageSize");


        if (pageIndexS == null) {
            throw new IllegalArgumentException("当前页不能为空");
        }

        if (pageSizeS == null) {
            throw new IllegalArgumentException("每页条数不能为空");
        }

        Integer pageIndex = null;
        Integer pageSize = null;

        try {
            pageIndex = Integer.parseInt(pageIndexS);
            pageSize = Integer.parseInt(pageSizeS);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("当前页或每页条数必须是数字");
        }

        PageHelper.startPage(pageIndex, pageSize);

        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        List<TbContent> tbContents = this.contentMapper.selectByExample(example);

        PageInfo<TbContent> pageInfo = new PageInfo<>(tbContents);

        Map<String, Object> objectMap = new HashMap<>();

        objectMap.put("list", tbContents);
        objectMap.put("totalSize", pageInfo.getTotal());


        return objectMap;
    }

    @Override
    public TbContent findOne(String idStr) {
        if (idStr == null) {
            throw new IllegalArgumentException("主键不能为空");
        }

        Long id = null;

        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("主键必须是数字");
        }

        TbContent content = this.contentMapper.selectByPrimaryKey(id);


        return content;
    }

    @Override
    public void save(TbContent content) {

        if (content == null) {
            throw new IllegalArgumentException("广告不能为空");
        }


        if(content.getId() == null) {
            int insert = this.contentMapper.insert(content);
            if (insert != 1)
                throw new DbException("广告插入失败");
            // 修改广告分类下的缓存
            this.redisTemplate.boundHashOps(ContentCategoryEnum.CACHE_HASH_KEY.getCode())
                    .delete(content.getCategoryId() + "");
        }else {

            // 查看是否是修改了广告分类
            TbContent content1 = this.contentMapper.selectByPrimaryKey(content.getId());
            Long oldCategory = content1.getCategoryId();

            // 删除旧分类下的缓存数据
            this.redisTemplate.boundHashOps(ContentCategoryEnum.CACHE_HASH_KEY.getCode())
                    .delete(oldCategory + "");

            int update = this.contentMapper.updateByPrimaryKeySelective(content);
            if (update != 1)
                throw new DbException("广告更新失败");

            // 判断是否修改 没有跟换分类无需更新缓存
            if (!oldCategory.equals(content.getCategoryId()))
                this.redisTemplate.boundHashOps(ContentCategoryEnum.CACHE_HASH_KEY.getCode())
                    .delete(content.getCategoryId() + "");

        }


    }

    @Override
    public void delete(String[] ids) {

        if (ids == null) {
            throw new IllegalArgumentException("主键不能为空");
        }

        List<Long> list = new ArrayList<>();

        for (String idStr : ids) {
            try {
                list.add(Long.parseLong(idStr));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("主键必须是数字");
            }
        }

        for (Long aLong : list) {
            TbContent old = this.contentMapper.selectByPrimaryKey(aLong);
            this.redisTemplate.boundHashOps(ContentCategoryEnum.CACHE_HASH_KEY.getCode())
                    .delete(old.getCategoryId() + "");
            int i = this.contentMapper.deleteByPrimaryKey(aLong);
            if (i != 1)
                throw new DbException("记录删除失败");
        }


    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<TbContent> findByCategoryId(String categoryIdS) {

        if (categoryIdS == null) {
            throw new IllegalArgumentException("广告分类id不能为空");
        }

        Long categoryId = null;

        try {
            categoryId = Long.parseLong(categoryIdS);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("主键必须是数字");
        }
        List<TbContent> contentList = null;

        try {
            // 缓存中是否存在
            Boolean hasKey = this.redisTemplate.boundHashOps(ContentCategoryEnum.CACHE_HASH_KEY.getCode()).hasKey(categoryIdS);
            if (hasKey) {
                contentList = (List<TbContent>) this.redisTemplate.boundHashOps(ContentCategoryEnum.CACHE_HASH_KEY.getCode()).get(categoryIdS);
                System.out.println("从缓存中读取数据");
                return contentList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("缓存异常。。。1"); // 规避
        }

        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(ContentCategoryEnum.ENABLE.getCode());
        criteria.andCategoryIdEqualTo(categoryId);
        example.setOrderByClause("sort_order ASC");
        contentList = this.contentMapper.selectByExample(example);

        try {
            // 放入
            this.redisTemplate.boundHashOps(ContentCategoryEnum.CACHE_HASH_KEY.getCode()).put(categoryIdS, contentList);
            System.out.println("从数据库读取 并放入缓存");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("缓存异常。。。2");
        }

        return contentList;
    }
}
