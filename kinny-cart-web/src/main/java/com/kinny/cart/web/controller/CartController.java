package com.kinny.cart.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.kinny.cart.service.CartService;
import com.kinny.pojo.group.CarGroup;
import com.kinny.util.CookieUtil;
import com.kinny.vo.ResponseVo;
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

    @Reference
    private CartService cartService;

    @RequestMapping("/CartList")
    public ResponseVo<List<CarGroup>> findCartList(HttpServletRequest request) {
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        cartListString = cartListString == null || cartListString == ""? "[]": cartListString;
        List<CarGroup> carGroupList = JSON.parseArray(cartListString, CarGroup.class);
        return new ResponseVo<>(true, carGroupList);
    }

    @RequestMapping("/addToCarts")
    public ResponseVo addGoodsToCartList(
            @RequestParam(required = true) Long itemId,
            @RequestParam(required = true) Integer num,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        try {
            // 从cookie中获取购物车列表
            ResponseVo<List<CarGroup>> responseVo = this.findCartList(request);
            List<CarGroup> carGroupList = responseVo.getData();
            // 添加商品到购物车
            carGroupList = this.cartService.addGoodsToCartList(carGroupList, itemId, num);
            // 放入cookie
            String s = JSON.toJSONString(carGroupList);
            CookieUtil.setCookie(request, response, "cartList", s, 3600 * 24, "UTF-8");
            return new ResponseVo(true, "添加购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "添加购物车失败");
        }

    }

}
