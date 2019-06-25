package com.kinny.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kinny.common.pojo.em.SellerEnum;
import com.kinny.mapper.TbSellerMapper;
import com.kinny.pojo.TbSeller;
import com.kinny.pojo.TbSellerExample;
import com.kinny.sellergoods.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/8 - 10:43
 */
@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private TbSellerMapper sellerMapper;

    @Override
    public void add(TbSeller seller) {

        if (seller == null) {
            throw new IllegalArgumentException("商家信息不能为空");
        }

        seller.setStatus(SellerEnum.NOT_CHECK.getCode()); // 未审核

        seller.setCreateTime(new Date());

        int i = this.sellerMapper.insert(seller);

        if (i != 1)
            throw new RuntimeException("新增商家信息失败");

    }

    @Override
    public Map<String, Object> findPage(Map<String, Object> map) {

        String pageIndexStr = (String) map.get("pageIndex");
        String pageSizeStr = (String) map.get("pageSize");
        String status = (String) map.get("status");
        String name = (String) map.get("name");
        String nickName = (String) map.get("nickName");



        if (StringUtils.isEmpty(pageIndexStr)) {
            throw new IllegalArgumentException("当前页不能为空");
        }
        if (StringUtils.isEmpty(pageSizeStr)) {
            throw new IllegalArgumentException("每页条数不能为空");
        }
        if (StringUtils.isEmpty(status)) {
            throw new IllegalArgumentException("商家状态不能为空");
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
        TbSellerExample example = new TbSellerExample();
        TbSellerExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }

        if (!StringUtils.isEmpty(nickName)) {
            criteria.andNickNameLike("%" + nickName + "%");
        }
        if (!StringUtils.isEmpty(status)) {
            criteria.andStatusEqualTo(status);
        }
        List<TbSeller> sellerList = this.sellerMapper.selectByExample(example);
        PageInfo<TbSeller> pageInfo = new PageInfo<>(sellerList);
        Map<String, Object> map1 = new HashMap<>();
        map1.put("list", pageInfo.getList());
        map1.put("totalNum", pageInfo.getTotal());
        return map1;
    }

    @Override
    public TbSeller findOne(String sellerId) {
        if (sellerId == null) {
            throw new IllegalArgumentException("商家主键不能为空");
        }

        return this.sellerMapper.selectByPrimaryKey(sellerId);
    }

    @Override
    public void updateStatus(String sellerId, String status) {

        if (sellerId == null) {
            throw new IllegalArgumentException("商家主键不能为空");
        }
        if (status == null) {
            throw new IllegalArgumentException("状态不能为空");
        }

        TbSeller seller = new TbSeller();
        seller.setStatus(status);
        seller.setSellerId(sellerId);
        int i = this.sellerMapper.updateByPrimaryKeySelective(seller);
        if (i != 1)
            throw new RuntimeException("修改商家状态失败");

    }
}
