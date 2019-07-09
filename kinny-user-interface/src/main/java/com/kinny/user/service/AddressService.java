package com.kinny.user.service;

import com.kinny.pojo.TbAddress;

import java.util.List;

/**
 *  用户收货地址服务
 * @author qgy
 * @create 2019/7/8 - 9:31
 */
public interface AddressService {

    /**
     *  获取当前认证实体下的收货地址
     * @param pricipal
     * @return
     */
    public List<TbAddress> findByPrincipal(String pricipal);

}
