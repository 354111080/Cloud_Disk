package com.eton.utils;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.HashMap;

@Component
public class ServletContextUtil {

    /**
     * 60秒后可重新发送此邮件
     * @param emailMap
     * @param email
     */
    @Async
    public void removeEmailInContext(HashMap<String, Boolean> emailMap, String email){
        try {
            Thread.sleep(60000);
            emailMap.remove(email);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证邮件是否在servletContext中
     * @param emailMap
     * @param email
     * @return true 为已存在 ， false为不存在
     */
    public static boolean checkEmailInContext(HashMap<String,Boolean> emailMap, String email){
        if(emailMap.get(email) != null && true == emailMap.get(email)){
            return true;
        }
        return false;
    }

}
