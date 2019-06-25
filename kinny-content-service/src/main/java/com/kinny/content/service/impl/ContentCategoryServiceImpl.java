package com.kinny.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kinny.common.exception.DbException;
import com.kinny.common.pojo.em.ContentCategoryEnum;
import com.kinny.content.service.ContentCategoryService;
import com.kinny.mapper.TbContentCategoryMapper;
import com.kinny.pojo.TbContentCategory;
import com.kinny.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/25 - 9:38
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired // 自动注入实例 反转资源获取方向
    private TbContentCategoryMapper contentCategoryMapper;


    @Override
    public List<TbContentCategory> findAll() {
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(ContentCategoryEnum.ENABLE.getCode());
        List<TbContentCategory> tbContentCategories = this.contentCategoryMapper.selectByExample(example);
        return tbContentCategories;
    }

    @Override
    public Map<String, Object> findPage(Map<String, Object> map) {

        String pageIndexS = (String) map.get("pageIndex");
        String pageSizeS = (String) map.get("pageSize");

        TbContentCategory contentCategory = (TbContentCategory) map.get("contentCategory");

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

        TbContentCategoryExample contentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = contentCategoryExample.createCriteria();
        if (contentCategory != null) {

            if(contentCategory.getName() != null && !("".equals(contentCategory.getName()))) {
                criteria.andNameLike("%" + contentCategory.getName() + "%");
            }

        }
        criteria.andStatusEqualTo(ContentCategoryEnum.ENABLE.getCode());
        List<TbContentCategory> contentCategories = this.contentCategoryMapper.selectByExample(contentCategoryExample);

        PageInfo<TbContentCategory> pageInfo = new PageInfo<>(contentCategories);

        Map<String, Object> objectMap = new HashMap<>();

        objectMap.put("list", pageInfo.getList());
        objectMap.put("totalSize", pageInfo.getTotal());

        return objectMap;
    }

    @Override
    public TbContentCategory findOne(String idStr) {
        if (idStr == null) {
            throw new IllegalArgumentException("广告主键不能为空");
        }

        Long id = null;

        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("广告主键必须是数字");
        }

        return this.contentCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(TbContentCategory contentCategory) {

        if (contentCategory == null) {
            throw new IllegalArgumentException("广告分类不能为空");
        }

        if(contentCategory.getId() != null) {
            this.update(contentCategory);
        }else {
            this.insert(contentCategory);
        }

    }

    @Override
    public void batchDelete(String[] ids) {

        if (ids == null) {
            throw new IllegalArgumentException("广告分类主键数组不能为空");
        }

        List<Long> list = new ArrayList<>();

        for (String id : ids) {
            try {
                list.add(Long.parseLong(id));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("广告主键必须是数字");
            }
        }

        // 校验通过

        for (Long aLong : list) {

            TbContentCategory contentCategory = new TbContentCategory();
            contentCategory.setId(aLong);
            contentCategory.setStatus(ContentCategoryEnum.UNENABLE.getCode());
            int i = this.contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);

            if (i != 1)
                throw new DbException("广告分类记录删除失败");
        }

    }

    private void insert(TbContentCategory contentCategory) {

        int insert = this.contentCategoryMapper.insert(contentCategory);
        if (insert != 1)
            throw new DbException("广告分类插入失败");

    }

    private void update(TbContentCategory contentCategory) {
        int update = this.contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
        if (update != 1)
            throw new DbException("广告分类修改失败");
    }
}
