package com.kinny.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kinny.mapper.TbSpecificationMapper;
import com.kinny.mapper.TbSpecificationOptionMapper;
import com.kinny.pojo.TbSpecification;
import com.kinny.pojo.TbSpecificationExample;
import com.kinny.pojo.TbSpecificationOption;
import com.kinny.pojo.TbSpecificationOptionExample;
import com.kinny.pojo.group.SpecificationGroup;
import com.kinny.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/6 - 9:14
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {


    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;


    @Override
    public List<TbSpecification> findAll() {
        return this.specificationMapper.selectByExample(null);
    }

    @Override
    public Map<String, Object> findPage(Map<String, Object> paramMap) {

        // 获取参数
        String pageIndexStr = (String) paramMap.get("pageIndex");
        String pageSizeStr = (String) paramMap.get("pageSize");
        String specName = (String) paramMap.get("specName");

        Integer pageIndex = null;

        Integer pageSize = null;



        // 检验非空
        if (pageIndexStr == null || "".equals(pageIndexStr)) {
            throw new IllegalArgumentException("当前页不能为空");
        }

        if (pageSizeStr == null || "".equals(pageSizeStr)) {
            throw new IllegalArgumentException("每页记录数不能为空");
        }

        // 数字
        try {
            pageIndex = Integer.parseInt(pageIndexStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("当前页必须是数字");
        }

        try {
            pageSize = Integer.parseInt(pageSizeStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("每页记录数必须是数字");
        }


        PageHelper.startPage(pageIndex, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        TbSpecificationExample.Criteria criteria = example.createCriteria();
        if (specName != null && !("".equals(specName))) {
            criteria.andSpecNameLike( "%" + specName + "%");
        }

        List<TbSpecification> specificationList = this.specificationMapper.selectByExample(example);

        PageInfo<TbSpecification> pageInfo = new PageInfo<>(specificationList);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalNum", pageInfo.getTotal());
        resultMap.put("list", pageInfo.getList());

        return resultMap;
    }

    @Override
    public SpecificationGroup findOne(String idStr) {

        if (idStr == null) {
            throw new IllegalArgumentException("规格id不能为空");
        }

        Long id = null;

        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("规格id必须是数字");
        }


        TbSpecification specification = this.specificationMapper.selectByPrimaryKey(id);

        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(specification.getId());
        List<TbSpecificationOption> specificationOptionList = this.specificationOptionMapper.selectByExample(example);

        SpecificationGroup specificationGroup = new SpecificationGroup(specification, specificationOptionList);

        return specificationGroup;
    }

    // todo 事务
    @Override
    public void cascadeAdd(SpecificationGroup specificationGroup) {

        TbSpecification specification = specificationGroup.getSpecification();

        List<TbSpecificationOption> specificationOptionList = specificationGroup.getSpecificationOptionList();

        int i = this.specificationMapper.insertSelective(specification);

        if (i != 1) {
            throw new RuntimeException("新增规格失败");
        }

        batchAddSpecificationOptionList(specification.getId(), specificationOptionList);

    }

    private void batchAddSpecificationOptionList(Long specificationId, List<TbSpecificationOption> specificationOptionList) {
        for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
            tbSpecificationOption.setSpecId(specificationId);
            int i1 = this.specificationOptionMapper.insertSelective(tbSpecificationOption);
            if (i1 != 1) {
                throw new RuntimeException("新增规格选项失败");
            }
        }
    }

    @Override
    public void cascadeUpdate(SpecificationGroup specificationGroup) {
        TbSpecification specification = specificationGroup.getSpecification();

        List<TbSpecificationOption> specificationOptionList = specificationGroup.getSpecificationOptionList();

        int i = this.specificationMapper.updateByPrimaryKeySelective(specification);

        if (i != 1) {
            throw new RuntimeException("修改规格失败");
        }

        // 删除原来参照的从表记录
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(specification.getId());
        int i1 = this.specificationOptionMapper.deleteByExample(example);

        this.batchAddSpecificationOptionList(specification.getId(), specificationOptionList);

    }

    @Override
    public void batchRemove(String[] ids) {

        if (ids == null) {
            throw new IllegalArgumentException("要移除的规格主键不能为空");
        }

        List<Long> idList = new ArrayList<>();

        for (String id : ids) {
            try {
                idList.add(Long.parseLong(id));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("要移除的规格主键必须是数字");
            }
        }

        for (Long id : idList) {
            /**
             *  当主表记录被从表记录参照使该主表记录不允许被删除
             *  只有把从表中所有关联该主表记录的所有从表记录删除后
             *  该主表记录才允许被删除
             *  还有一种情况删除主表记录时可以级联删除从表中所有关联该主表记录的所有从表记录
             */
            TbSpecificationOptionExample specificationOptionExample = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = specificationOptionExample.createCriteria();
            criteria.andSpecIdEqualTo(id);
            this.specificationOptionMapper.deleteByExample(specificationOptionExample);
            TbSpecificationExample specificationExample = new TbSpecificationExample();
            TbSpecificationExample.Criteria criteria1 = specificationExample.createCriteria();
            criteria1.andIdEqualTo(id);
            int i = this.specificationMapper.deleteByExample(specificationExample);
            if (i != 1)
                throw new RuntimeException("删除规格失败");
        }


    }
}
