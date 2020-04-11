package com.eton.controller;

import com.eton.pojo.User;
import com.eton.service.UserService;
import com.eton.utils.LanguageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        String email = httpServletRequest.getParameter("email");
        String password = httpServletRequest.getParameter("password");
        String isAutoLogin = httpServletRequest.getParameter("autoLogin");
        if(email != null && password != null && userService.checkUserLoginInfo(email,password)){
            userService.login(httpServletRequest,httpServletResponse,email,password,isAutoLogin);
            httpServletRequest.getSession().setMaxInactiveInterval(120*60);//设置session无活动有效时长
            return "1";//登录成功
        }
        return "0";//信息有误
    }


    @RequestMapping("/main")
    public String toService(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user != null){
            request.getSession().setAttribute("dir",user.getId().toString());//默认显示路径设置为root
            LanguageUtil.setLanguage(request,model);
            return "main";
        }
        return "index";
    }

    @RequestMapping("/profile")
    public String toProfile(HttpServletRequest request,Model model){
        LanguageUtil.setLanguage(request,model);
        return "profile";
    }

    @RequestMapping("/findPassword")
    public String toFindPassword(){
        return "findpassword";
    }

}
