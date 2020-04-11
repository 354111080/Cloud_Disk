package com.eton.utils;

import org.springframework.ui.Model;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class LanguageUtil {

    public static void setLanguage(HttpServletRequest request, Model model){
        String langSet = null;//语言设置
        if(request.getCookies() != null){
            //在cookie中寻找语言记录
            for(Cookie cookie : request.getCookies()){
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
    }

}
