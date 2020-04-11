package com.eton.controller;

import com.eton.pojo.User;
import com.eton.service.UserService;
import com.eton.utils.ServletContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

@Controller
public class RegisterController {

    private static HashMap<String,Boolean> emailMap = new HashMap<String,Boolean>();

    //邮件发送
    private JavaMailSenderImpl mailSender;

    //用户业务
    private UserService userService;

    //
    private ServletContextUtil servletContextUtil;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    public void setServletContextUtil(ServletContextUtil servletContextUtil) {
        this.servletContextUtil = servletContextUtil;
    }

    /**
     * 发送验证码到指定的邮件
     * @param email
     */
    public void sendVerifyCode(HttpServletRequest httpServletRequest, String email){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("[EtonCloud] 注册验证码");
        Random random = new Random();
        String verifyCode = "";
        for(int i = 0;i<6;i++){
            verifyCode+= random.nextInt(10);
        }
        simpleMailMessage.setText("您的验证码为："+verifyCode);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom("354114080@qq.com");
        mailSender.send(simpleMailMessage);
        this.setEmailInContext(httpServletRequest,email,verifyCode);
    }

    @GetMapping("/checkEmailExist")
    @ResponseBody
    public String checkEmailExist(String email,HttpServletRequest request){
        String res = userService.findUserByEmail(email);
        if("1".equals(res)){
            sendVerifyCode(request,email);
            return "1";
        }else {
            return "0";
        }
    }

    /**
     * 把已发送验证码的邮件放入sertvletContext中
     * @param httpServletRequest
     * @param email
     */
    private void setEmailInContext(HttpServletRequest httpServletRequest, String email,String verifyCode){
        emailMap.put(email,true);
        httpServletRequest.getSession().setAttribute("email",email);
        httpServletRequest.getSession().setAttribute("verifyCode",verifyCode);
    }


    @GetMapping("/checkEmail")
    @ResponseBody
    public String checkEmail(HttpServletRequest httpServletRequest, String email){
        if(ServletContextUtil.checkEmailInContext(emailMap,email)){
            return "2";
        }
        String res = userService.findUserByEmail(email);
        if("0".equals(res)){
            try{
                this.sendVerifyCode(httpServletRequest,email);
                servletContextUtil.removeEmailInContext(emailMap, email);
            }catch (Exception e){
                return "-1";//发送失败
            }
        }
        return res;
    }


    @GetMapping("/checkVerifyCode")
    @ResponseBody
    public String checkVerifyCode(HttpServletRequest httpServletRequest, String verifyCode){
        String verifyCodeInSession = (String) httpServletRequest.getSession().getAttribute("verifyCode");
        String res = null;
        if(verifyCodeInSession == null){
            res = "-1";
        }else if(!verifyCodeInSession.equals(verifyCode)){
            res = "0";
        }else if(verifyCodeInSession.equals(verifyCode)){
            res = "1";
        }
        return res;
    }


    @PostMapping("/signup")
    @ResponseBody
    public String signUp(HttpServletRequest httpServletRequest) throws IOException {
        String verifyCode = httpServletRequest.getParameter("verifyCode");
        if(verifyCode!=null && verifyCode.equals(httpServletRequest.getSession().getAttribute("verifyCode"))){//判断验证码
            String username = httpServletRequest.getParameter("username");//获取用户名
            String password = httpServletRequest.getParameter("password");//获取密码
            String email = (String) httpServletRequest.getSession().getAttribute("email");//获取邮箱

            User user = new User(null,email,username,password,null);//通过获取到的信息构建用户对象
            int res = userService.addNewUser(user);
            if(res == -1){
                return "-1";//返回消息至客户端，通知其输入信息有误
            }else if(res == -2){
                return "-2";//表示此邮箱已被注册
            }else if(res>0){
                httpServletRequest.getSession().removeAttribute("email");
                httpServletRequest.getSession().removeAttribute("verifyCode");//清除session中保留的验证信息
                return "2";//代表注册成功
            }
        }
        return "0";//返回消息至客户端，通知其注册失败
    }


    @GetMapping("/register_success")
    public String registerSuccess(){
        return "skip";
    }

    @PostMapping("/changeForgetPassword")
    @ResponseBody
    public String changePassword(String newPass,String verifyCode,HttpServletRequest request){
        HttpSession session = request.getSession();
        String verifyCodeInSession = (String) session.getAttribute("verifyCode");
        if(verifyCode!=null && newPass.length()>=8 && newPass.length()<=12){
            if(!verifyCode.equals(verifyCodeInSession)){
                return "-1";//验证码错误
            }
            String email = (String) session.getAttribute("email");
            User user = userService.getUserInfoForResetPassword(email);
            if(user.equals(newPass)){
                request.getSession().removeAttribute("email");
                request.getSession().removeAttribute("verifyCode");//清除session中保留的验证信息
                return "1";//重置成功(新密码与原密码一致)
            }
            boolean b = userService.modifyPassword(user.getId(),newPass);
            if(b){
                return "1";//重置成功
            }
        }
        return "0";//重置失败
    }
}
