package com.eton.service;

import com.eton.dao.UserMapper;
import com.eton.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserService {

    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 通过email信息查询用户
     * @param email
     * @return 如果返回值为1为查询到用户的情况，为0则没有查到用户
     */
    public String findUserByEmail(String email){
        String result = userMapper.selectUserByEmail(email);
        if(result != null){
            return "1";//表示用户已存在
        }
        return "0";//表示用户已不存在
    }

    /**
     * 添加新用户
     * @param user
     * @return
     */
    public int addNewUser(User user){
//        String emailPattern = "/^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]{2,31}@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$/";
//        String passwordPattern = "/^[^\\u4e00-\\u9fa5]+$/";
        if("1".equals(findUserByEmail(user.getEmail()))){
            return -2;//表示此邮箱已被注册
        }
        if(user.getUsername().length()>0 && user.getUsername().length()<=14 && user.getPassword().length()>=8 && user.getPassword().length()<=12){
            return userMapper.addNewUser(user);//执行用户添加操作
        }
        return -1;//输入信息不符合要求
    }

    /**
     * 验证用户的登录信息
     * @param email
     * @param password
     * @return true代表信息正确，false代表信息错误
     */
    public boolean checkUserLoginInfo(String email, String password){
        User user = userMapper.checkUserByEmail(email);
        if(user != null && user.getPassword().equals(password)){//判断是否满足邮箱正确且密码正确
            return true;
        }
        return false;
    }

    public User getUserInfoForResetPassword(String email){
        return userMapper.checkUserByEmail(email);
    }

    /**
     * 获取用信息
     * @param email
     * @return
     */
    public User getUserInfo(String email){
        User user = userMapper.getUser(email);
        return user;
    }

    /**
     * 执行登录操作
     * @param request
     * @param response
     */
    public void login(HttpServletRequest request, HttpServletResponse response, String email, String password, String isAutoLogin){
        //自动登录
        /**
         * 将邮箱和密码信息放入cookie
         */
        if("on".equals(isAutoLogin)){
            Cookie emailCookie = new Cookie("email",email);
            Cookie passwordCookie = new Cookie("password",password);
            emailCookie.setMaxAge(60*60*24*30*2);
            passwordCookie.setMaxAge(60*60*24*30*2);
            emailCookie.setPath("/");
            passwordCookie.setPath("/");
            response.addCookie(emailCookie);
            response.addCookie(passwordCookie);
        }
        User user = this.getUserInfo(email);
        request.getSession().setAttribute("user",user);//把登录成功的用户对象放入cookie
    }

    /**
     * 执行自动登录
     * @param request
     * @param email
     * @param password
     */
    public void autoLogin(HttpServletRequest request,String email, String password){
        request.getSession().setAttribute("user",this.getUserInfo(email));//把登录成功的用户对象放入session
    }

    /**
     * 为用户添加头像
     * @param userId
     * @param profilePic
     * @return
     */
    public boolean addProfilePic(Integer userId,String profilePic){
        User user = new User();
        user.setId(userId);
        user.setProfile_pic(profilePic);
        if(userMapper.addProfilePic(user)>0){
            return true;
        }
        return false;
    }

    //获取用户的头像信息
    public String getUserProfilePic(Integer userId){
        return userMapper.getUserProfilePic(userId);
    }

    //修改用户名
    public boolean modifyUsername(Integer userId,String userName){
        User user = new User();
        user.setId(userId);
        user.setUsername(userName);
        int res = userMapper.updateUsername(user);
        if(res > 0){
            return true;
        }
        return false;
    }

    public boolean modifyPassword(Integer userId,String password){
        User user = new User();
        user.setId(userId);
        user.setPassword(password);
        int res = userMapper.updatePassword(user);
        if(res > 0){
            return true;
        }
        return false;
    }

}
