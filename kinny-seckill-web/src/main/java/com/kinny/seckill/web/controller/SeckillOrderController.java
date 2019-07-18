package com.kinny.seckill.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.common.exception.CacheException;
import com.kinny.seckill.service.SeckillOrderService;
import com.kinny.vo.ResponseVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qgy
 * @create 2019/7/17 - 15:02
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;


    /**
     *  秒杀下单 （抢购）
     *   shiro不对该访问路径进行authc拦截 会导致重定向到cas定制的登录页面
     *   用户体验不好 采用弹框提示
     * @param id
     * @return
     */
    @RequestMapping("/submitOrder")
    public ResponseVo submitOrder(String id) {
        String principal = (String) SecurityUtils.getSubject().getPrincipal();
        if (principal == null) {
            return new ResponseVo(false, "请先去登录");
        }
        try {
            this.seckillOrderService.submitOrder(principal, id);
        } catch (IllegalArgumentException e) {
            return new ResponseVo(false, e.getMessage());
        } catch (CacheException e) {
            return new ResponseVo(false, e.getMessage());
        }
          catch (Exception e) {
            return new ResponseVo(false, "秒杀下单失败");
        }

        return new ResponseVo(true, "抢购成功 请及时支付");
    }


}
