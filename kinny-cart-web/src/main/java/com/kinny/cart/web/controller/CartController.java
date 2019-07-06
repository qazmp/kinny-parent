package com.kinny.cart.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.kinny.cart.service.CartService;
import com.kinny.pojo.group.CarGroup;
import com.kinny.util.CookieUtil;
import com.kinny.vo.ResponseVo;
import org.apache.shiro.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author qgy
 * @create 2019/7/4 - 15:49
 */
@RequestMapping("/cart")
@RestController
public class CartController {

    @Reference(timeout = 5000)
    private CartService cartService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/CartList")
    public ResponseVo<List<CarGroup>> findCartList(HttpServletRequest request, HttpServletResponse response) {
        String principal = (String) SecurityUtils.getSubject().getPrincipal();
        System.err.println("principal = " + principal);

        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if(cartListString == null || cartListString.equals("")) {
            cartListString = "[]";
        }
        List<CarGroup> carGroupList_cookie = JSON.parseArray(cartListString, CarGroup.class);

        if(principal == null) { // 匿名用户 使用本地购物车
            System.err.println("从cookie中获取购物车");
            return new ResponseVo<>(true, carGroupList_cookie);
        }else { // 认证 远程
            System.err.println("从redis中获取购物车");
            List<CarGroup> carGroupList_redis = this.cartService.findCartListFromRedis(principal);

            if(carGroupList_cookie.size() >= 1) {
                // 合并购物车列表 并清空本地购物车列表 存入远程购物车
                System.err.println("合并购物车列表");
                List<CarGroup> carGroupList = this.cartService.mergeCartList(carGroupList_redis, carGroupList_cookie);
                this.cartService.addGoodsToRedis(principal, carGroupList);
                CookieUtil.deleteCookie(request, response, "cartList");
                return new ResponseVo<>(true, carGroupList);
            }
            return new ResponseVo<>(true, carGroupList_redis);
        }

    }

    @RequestMapping("/addToCarts")
    public ResponseVo addGoodsToCartList(
            @RequestParam(required = true) Long itemId,
            @RequestParam(required = true) Integer num,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String principal = (String) SecurityUtils.getSubject().getPrincipal();
        System.err.println("principal = " + principal);
        try {
            // 从cookie redis中获取购物车列表
            ResponseVo<List<CarGroup>> responseVo = this.findCartList(request, response);
            List<CarGroup> carGroupList = responseVo.getData();
            // 添加商品到购物车
            carGroupList = this.cartService.addGoodsToCartList(carGroupList, itemId, num);
            if(principal == null) { // 匿名
                System.err.println("放入cookie购物车");
                // 放入cookie
                String s = JSON.toJSONString(carGroupList);
                CookieUtil.setCookie(request, response, "cartList", s, 3600 * 24, "UTF-8");
                return new ResponseVo(true, "添加购物车成功");
            }else { // 已经认证用户
                System.err.println("放入redis购物车");
                this.cartService.addGoodsToRedis(principal, carGroupList);
                return new ResponseVo(true, "添加购物车成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "添加购物车失败");
        }

    }

}
