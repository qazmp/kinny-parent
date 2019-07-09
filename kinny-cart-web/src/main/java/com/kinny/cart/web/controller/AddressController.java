package com.kinny.cart.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.pojo.TbAddress;
import com.kinny.user.service.AddressService;
import com.kinny.vo.ResponseVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qgy
 * @create 2019/7/8 - 9:34
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    @RequestMapping("/getPrincipalAddressList")
    public ResponseVo<List<TbAddress>> getPrincipalAddressList() {

        // 当前认证的实体
        String principal = (String) SecurityUtils.getSubject().getPrincipal();

        try {
            List<TbAddress> addressList = this.addressService.findByPrincipal(principal);
            return new ResponseVo<>(true, addressList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "获取收货地址列表失败");
        }

    }

}
