package com.eton.controller;

import com.eton.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String index(HttpServletRequest httpServletRequest, Model model){//获取语言环境信息并返回登录页
        String langSet = null;//语言设置
        if(httpServletRequest.getCookies() != null){
            String email = null;
            String password = null;

            //在cookie中寻找登录信息
            for(Cookie cookie : httpServletRequest.getCookies()){
                if("email".equals(cookie.getName())){
                    email = cookie.getValue();
                }
                if("password    ".equals(cookie.getName())){
                    password = cookie.getValue();
                }
            }
            if(email!=null && password!=null){
                boolean flag = userService.checkUserLoginInfo(email,password);
                if(flag){//如果信息正确直接进入主界面
                    userService.autoLogin(httpServletRequest,email,password);
                    return "main";
                }
            }

            //在cookie中寻找语言记录
            for(Cookie cookie : httpServletRequest.getCookies()){
                if("language".equals(cookie.getName())){
                    langSet = cookie.getValue();
                    break;
                }
            }
        }
        if(!StringUtils.isEmpty(langSet)){
            if("zh_CN".equals(langSet)){
                model.addAttribute("language","简体中文");
            }else if("zh_TW".equals(langSet)){
                model.addAttribute("language","繁體中文");
            }
        }else {
            model.addAttribute("language","简体中文");
        }
        return "index";
    }

    @RequestMapping("/register")
    public String toRegister(){
        return "register";
    }

    /**
     * 获取更改语言环境请求并使其存入cookie
     * @param httpServletRequest
     * @param httpServletResponse
     */
    @RequestMapping("/language")
    @ResponseBody
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        String lang = httpServletRequest.getParameter("lang");//获取请求的语言参数
        /**
         * 如果参数不为空
         */
        if(!StringUtils.isEmpty(lang)){
            Cookie cookie = new Cookie("language",lang);
            cookie.setMaxAge(60*60*24*30);//设置cookie有效时常
            httpServletResponse.addCookie(cookie);
        }
    }

}
