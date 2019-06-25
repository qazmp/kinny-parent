package com.kinny.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.common.exception.SMSException;
import com.kinny.user.service.UserService;
import com.kinny.common.exception.DbException;
import com.kinny.pojo.TbUser;
import com.kinny.vo.ResponseVo;
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
 * @create 2019/6/22 - 17:19
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;


    /**
     *  用户账号注册
     * @param user
     * @return
     */
    @RequestMapping("/registry")
    public ResponseVo registry(@Valid @RequestBody(required = true) TbUser user, BindingResult bindingResult,
                               @RequestParam(required = true) String smsCode) {

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
            this.userService.registry(user, smsCode);
        } catch (DbException e) {
            return new ResponseVo(false, e.getMessage());
        } catch (SMSException e) {
            return new ResponseVo(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "用户注册失败");
        }

        return new ResponseVo(true, "用户注册成功");
    }

    @RequestMapping("/sendSms")
    public ResponseVo sendSmsCode(String phone) {
        try {
            this.userService.sendSmsCode(phone);
        } catch (IllegalArgumentException e) {
            return new ResponseVo(false, e.getMessage());
        }
        catch (Exception e) {
            return new ResponseVo(false, "验证码发送失败");
        }
        return new ResponseVo(true, "验证码发送成功");
    }

}
