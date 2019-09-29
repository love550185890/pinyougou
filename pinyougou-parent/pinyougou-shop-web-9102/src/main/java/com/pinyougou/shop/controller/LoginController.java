package com.pinyougou.shop.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author 550185890@qq.com
 * @Date 2019年9月26日18:13:45
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/loginName")
    public Map name(){
        //获取当前登录人账号
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = new HashMap();
        map.put("loginName",userName);
        return map;
    }
}
