package com.kinny.user.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/7/3 - 15:08
 */
@RequestMapping("/login")
@RestController
public class LoginController {


    @RequestMapping("/principal")
    public Map<String, Object> getPrincipal() {
        String principal = (String) SecurityUtils.getSubject().getPrincipal();
        Map<String, Object> map = new HashMap<>();
        map.put("name", principal);
        return map;
    }


}
