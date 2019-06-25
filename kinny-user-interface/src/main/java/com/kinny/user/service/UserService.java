package com.kinny.user.service;

import com.kinny.pojo.TbUser;

/**
 * @author qgy
 * @create 2019/6/22 - 17:12
 */
public interface UserService {

    /**
     *  注册  验证验证码
     * @param user
     * @param smsCode
     */
    public void registry(TbUser user, String smsCode);

    /**
     * 发送验证码
     * @param phone
     */
    public void sendSmsCode(String phone);


}
