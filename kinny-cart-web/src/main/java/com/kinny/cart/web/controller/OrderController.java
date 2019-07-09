package com.kinny.cart.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.common.exception.DbException;
import com.kinny.order.service.OrderService;
import com.kinny.pojo.TbOrder;
import com.kinny.vo.ResponseVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qgy
 * @create 2019/7/9 - 14:17
 */
@RequestMapping("/order")
@RestController
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/createOrder")
    public ResponseVo createOrder(@RequestBody(required = true)TbOrder order) {
        String principal = (String) SecurityUtils.getSubject().getPrincipal();
        order.setUserId(principal);
        order.setSourceType("2");
        try {
            this.orderService.addOrderAndClear(order);
        } catch (IllegalArgumentException e) {
            return new ResponseVo(false, e.getMessage());
        } catch (DbException e) {
            return new ResponseVo(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "用户订单失败");
        }
        return new ResponseVo(true, "用户订单成功");
    }

}
