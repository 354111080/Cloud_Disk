package com.eton.config;

import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * 多语言解析器
 */
public class AppLocaleResolver implements LocaleResolver {

    /**
     * 解析请求并返回语言环境对象
     * @param httpServletRequest
     * @return 语言环境对象
     */
    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        Locale locale;
        /**
         * 从浏览器cookie中检索是否有语言设置信息
         */
        if(httpServletRequest.getCookies() != null){
            for(Cookie cookie : httpServletRequest.getCookies()){
                if("language".equals(cookie.getName())){
                    String langCookie = cookie.getValue();
                    String[] strings = langCookie.split("_");
                    locale = new Locale(strings[0],strings[1]);
                    return locale;
                }
            }
        }
        locale = new Locale("zh","CN");//如果cookie中无参数把简体中文设置为默认语言
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

    }

}
