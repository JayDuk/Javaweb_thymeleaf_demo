package com.example.controller;

import com.mvc.annotation.Component;
import com.mvc.annotation.Controller;
import com.mvc.annotation.RequestMapping;

import static com.mvc.util.StrUtil.FORWARD;

/**
 * @author javaok
 * 2022/12/2 14:30
 */
@Component
@Controller
@RequestMapping("/login")
public class LoginController {

    @RequestMapping
    String loginPage(){
        return FORWARD+"/static/login.html";
    }
}
