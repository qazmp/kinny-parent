package com.kinny.cart.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.pay.service.PayService;
import com.kinny.util.IdWorker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author qgy
 * @create 2019/7/11 - 9:37
 */
@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference(timeout = 5000)
    private PayService zhiFuBaoPayServiceImpl;

    @RequestMapping("/createNative")
    public Map<String, Object> createNative() {
        IdWorker idWorker = new IdWorker();
        Map<String, Object> aNative = this.zhiFuBaoPayServiceImpl.createNative(idWorker.nextId() + "", "1");
        return aNative;
    }




}
