package com.kinny.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.kinny.mapper.TbAddressMapper;
import com.kinny.pojo.TbAddress;
import com.kinny.pojo.TbAddressExample;
import com.kinny.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author qgy
 * @create 2019/7/8 - 9:33
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private TbAddressMapper addressMapper;

    @Override
    public List<TbAddress> findByPrincipal(String pricipal) {

        TbAddressExample example = new TbAddressExample();
        TbAddressExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(pricipal);
        example.setOrderByClause("is_default Desc");
        return this.addressMapper.selectByExample(example);
    }
}
