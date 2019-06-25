package com.kinny.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.pojo.TbSeller;
import com.kinny.sellergoods.service.SellerService;
import com.kinny.vo.ResponseVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.codec.CodecException;
import org.apache.shiro.crypto.UnknownAlgorithmException;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author qgy
 * @create 2019/5/8 - 10:41
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    @Value("${shiro.password.md5.hashIterations}")
    private Integer hashIterations;


    @RequestMapping("/add")
    public ResponseVo add(@Valid @RequestBody(required = true) TbSeller seller, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            StringBuilder stringBuilder = new StringBuilder();
            for (FieldError fieldError : fieldErrors) {
                stringBuilder.append(fieldError.getDefaultMessage());
                stringBuilder.append(";");
            }
            return new ResponseVo(false, stringBuilder.toString());
        }

        try {
            // 对用户密码进行MD5盐值加密
            String sellerId = seller.getSellerId();
            String password = seller.getPassword();
            SimpleHash simpleHash = new SimpleHash("MD5", password, seller.getSellerId(), this.hashIterations);
            seller.setPassword(simpleHash.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "保存商家信息失败");
        }
        try {

            this.sellerService.add(seller);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseVo(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "保存商家信息失败");
        }

        return new ResponseVo(true, "保存商家信息成功");
    }


    @RequestMapping("/login.do")
    public ResponseVo authentication(@RequestParam(required = true) String username,
                                 @RequestParam(required = true) String password) {

        // 封装为token
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            return new ResponseVo(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "身份登录失败");
        }

        return new ResponseVo(true, "身份登录成功");
    }

    @RequestMapping("/getPrincipal")
    public ResponseVo<TbSeller> getPrincipal() {

        TbSeller principal = (TbSeller) SecurityUtils.getSubject().getPrincipal();

        if (principal == null) {
            return new ResponseVo<>(false, "获取主体失败");
        }

        return new ResponseVo<>(true, principal);
    }






}
