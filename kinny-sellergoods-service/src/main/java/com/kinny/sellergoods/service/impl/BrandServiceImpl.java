package com.kinny.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kinny.mapper.TbBrandMapper;
import com.kinny.pojo.TbBrand;
import com.kinny.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/2 - 16:17
 */
@Service
public class BrandServiceImpl implements BrandService {


    @Autowired
    private TbBrandMapper tbBrandMapper;


    @Override
    public List<TbBrand> findAll() {

        return this.tbBrandMapper.selectByExample(null);
    }

    @Override
    public Map<String, Object> findPage(Map<String, Object> map) {

        // 获取参数
        String pageIndexStr = (String) map.get("pageIndex");

        String pageSizeStr = (String) map.get("pageSize");

        int pageIndex = 1;

        int pageSize = 10;

        // 非空校验 @requestParam 已经约束 略

        // 数值类型校验
        try {
           pageIndex = Integer.parseInt(pageIndexStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw  new IllegalArgumentException("当前页数必须是数字");
        }

        try {
          pageSize =  Integer.parseInt(pageSizeStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw  new IllegalArgumentException("每页的记录条数必须是数字");
        }

        PageHelper.startPage(pageIndex, pageSize);

        List<TbBrand> brandList = this.tbBrandMapper.selectByExample(null);

        PageInfo<TbBrand> pageInfo = new PageInfo(brandList);

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("list", pageInfo.getList());

        resultMap.put("totalNum", pageInfo.getTotal());


        return resultMap;
    }

    @Override
    public TbBrand findOne(String idStr) {

        if (idStr == null) {
            throw new IllegalArgumentException("品牌主键不能为空");
        }

        Long id = null;

        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("品牌主键必须是数字");
        }

        return this.tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(TbBrand brand) {

        if (brand == null) {
            throw new IllegalArgumentException("新增或者修改数据不能为空");
        }

        if (brand.getId() != null) {
            // 修改
            int i = this.tbBrandMapper.updateByPrimaryKey(brand);
            if (i != 1)
                throw new RuntimeException("修改品牌记录失败");
        }else {
            int insert = this.tbBrandMapper.insert(brand);
            if (insert != 1)
                throw new RuntimeException("新增品牌记录失败");
        }


    }

    /**
     * 已改运行在事务中
     * @param ids
     */
    @Override
    public void batchDelete(List<String> ids) {

        List<Long> idList = new ArrayList<>();
        for (String id : ids) {
           if (id == null || id.equals(""))
               throw new IllegalArgumentException("品牌主键不能为空");
            try {
                long l = Long.parseLong(id);
                idList.add(l);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("品牌主键必须是数字");
            }
        }

        for (Long id : idList) {
            int i = this.tbBrandMapper.deleteByPrimaryKey(id);
            if (i != 1) {
                throw new RuntimeException("删除品牌记录失败");
            }
        }


    }
}
