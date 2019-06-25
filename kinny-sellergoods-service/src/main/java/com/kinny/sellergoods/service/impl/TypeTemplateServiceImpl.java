package com.kinny.sellergoods.service.impl;

import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kinny.common.exception.DbException;
import com.kinny.common.pojo.em.CacheEnum;
import com.kinny.mapper.TbSpecificationOptionMapper;
import com.kinny.mapper.TbTypeTemplateMapper;
import com.kinny.pojo.TbSpecificationOption;
import com.kinny.pojo.TbSpecificationOptionExample;
import com.kinny.pojo.TbTypeTemplate;
import com.kinny.pojo.TbTypeTemplateExample;
import com.kinny.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/7 - 9:49
 */
@Service(timeout = 5000)
public class TypeTemplateServiceImpl implements TypeTemplateService {


    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<TbTypeTemplate> findAll() {
        return this.typeTemplateMapper.selectByExample(null);
    }

    @Override
    public Map<String, Object> findPage(Map<String, Object> param) {

        String pageIndexStr = (String) param.get("pageIndex");
        String pageSizeStr = (String) param.get("pageSize");
        String name = (String) param.get("name");

        if (pageIndexStr == null) {
            throw new IllegalArgumentException("当前页不能为空");
        }
        if (pageSizeStr == null) {
            throw new IllegalArgumentException("每页条数不能为空");
        }

        Integer pageIndex = null;
        Integer pageSize = null;

        try {
            pageIndex = Integer.parseInt(pageIndexStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("当前页必须是数值");
        }
        try {
            pageSize = Integer.parseInt(pageSizeStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("每页条数必须是数值");
        }

        PageHelper.startPage(pageIndex, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        TbTypeTemplateExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        List<TbTypeTemplate> typeTemplateList = this.typeTemplateMapper.selectByExample(example);
        PageInfo<List<TbTypeTemplate>> pageInfo = new PageInfo(typeTemplateList);
        Map result = new HashMap();
        result.put("list", pageInfo.getList());
        result.put("totalNum", pageInfo.getTotal());

        cacheBrandAndSpecification();


        return result;
    }

    /**
     *  缓存品牌和规格以及规格选项
     */
    private void cacheBrandAndSpecification() {
        List<TbTypeTemplate> templateList = this.findAll();
        BoundHashOperations<String, Object, Object> brandHashOps = this.redisTemplate.boundHashOps(CacheEnum.CACHE_ID_BRAND.code);
        BoundHashOperations<String, Object, Object> specificationHashOps = this.redisTemplate.boundHashOps(CacheEnum.CACHE_ID_SPECIFICATION.code);
        for (TbTypeTemplate typeTemplate : templateList) {

            String brandIds = typeTemplate.getBrandIds();
            List<Map> brandList = JSON.parseArray(brandIds, Map.class);
            brandHashOps.put(typeTemplate.getId() + "", brandList);

            TbTypeTemplate template = this.findOneContainsSpecOptions(typeTemplate.getId() + "");
            List<Map> specifications = JSON.parseArray(template.getSpecIds(), Map.class);
            specificationHashOps.put(typeTemplate.getId() + "", specifications);

        }
    }

    @Override
    public TbTypeTemplate findOne(String idStr) {
        if (idStr == null) {
            throw new IllegalArgumentException("模板主键不能为空");
        }
        Long id = null;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("模板主键必须是数值");
        }

        return this.typeTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public TbTypeTemplate findOneContainsSpecOptions(String idStr) {
        if (idStr == null) {
            throw new IllegalArgumentException("模板主键不能为空");
        }
        Long id = null;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("模板主键必须是数值");
        }

        TbTypeTemplate tbTypeTemplate = this.typeTemplateMapper.selectByPrimaryKey(id);

        if (tbTypeTemplate == null) {
            throw new DbException("模板的数据不存在");
        }

        String specIds = tbTypeTemplate.getSpecIds();

        List<Map> list = JSON.parseArray(specIds, Map.class);

        for (Map map : list) {

            Integer id1 = (Integer) map.get("id");

            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(Long.parseLong(id1 + ""));
            List<TbSpecificationOption> tbSpecificationOptions = this.specificationOptionMapper.selectByExample(example);

            map.put("options", tbSpecificationOptions);

        }

        String specOp = JSON.toJSONString(list);

        tbTypeTemplate.setSpecIds(specOp);


        return tbTypeTemplate;
    }

    @Override
    public void save(TbTypeTemplate typeTemplate) {

        if (typeTemplate == null) {
            throw new IllegalArgumentException("模板不能为空");
        }

        if (typeTemplate.getId() != null) {

            int i = this.typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
            if (i != 1) {
                throw new RuntimeException("修改模板失败");
            }
        }else {

            int insert = this.typeTemplateMapper.insert(typeTemplate);

            if (insert != 1) {
                throw new RuntimeException("新增模板失败");
            }

        }


    }


    //todo spring 事务
    @Override
    public void batchDelete(String[] ids) {

        // 非空
        if (ids == null) {
            throw new IllegalArgumentException("模板主键不能为空");
        }

        List<Long> idList = new ArrayList<>();

        // 类型格式校验
        for (String id : ids) {
            try {
                idList.add(Long.parseLong(id));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("模板主键必须是数字");
            }
        }

        for (Long id : idList) {
            int i = this.typeTemplateMapper.deleteByPrimaryKey(id);
            if (i != 1) {
                throw new RuntimeException("删除模板失败");
            }
        }


    }
}
