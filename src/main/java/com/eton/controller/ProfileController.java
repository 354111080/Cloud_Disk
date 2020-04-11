package com.eton.controller;

import com.eton.pojo.User;
import com.eton.service.FileService;
import com.eton.service.UserService;
import com.eton.utils.FtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class ProfileController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 上传头像
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadProfilePic")
    @ResponseBody
    public String UploadProfilePic(@RequestParam("pic") MultipartFile file, HttpServletRequest request) throws IOException {
        if(file.getSize()>2097152l){
            return "-1";
        }
        String suffix = ".jpg";
        String fileName = "profilePic"+suffix;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        InputStream inputStream = file.getInputStream();
        boolean b = FtpUtil.upLoadProfilePic(user.getId(),inputStream,fileName);
        if (b){
            String picPath = "http://129.211.164.89:80/files/"+user.getId()+"_profilePic/"+fileName;
            String profilePic = userService.getUserProfilePic(user.getId());
            if(profilePic == null || "".equals(profilePic)){
                boolean b1 = userService.addProfilePic(user.getId(),picPath);
                if(b1){
                    user.setProfile_pic(picPath);
                    session.setAttribute("user",user);
                    return picPath;
                }else{
                    return "0";
                }
            }
            return picPath;
        }
        return "0";

    }

    @GetMapping("/modifyUsername")
    @ResponseBody
    public String modifyUserName(String username,HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user != null){
            if(user.getUsername().equals(username)){
                return "0";//用户名没发生改变
            }
            if(username != null && username.length()>0 && username.length()<=14){//对username进行限制
                boolean b = userService.modifyUsername(user.getId(),username);
                if(b){
                    user.setUsername(username);
                    session.setAttribute("user",user);
                    return "1";
                }
                return "-1";
            }
        }
        return "-1";
    }

    @PostMapping("/modifyPassword")
    @ResponseBody
    public String modifyPassword(String originalPass,String newPass, HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user != null){
            if(!user.getPassword().equals(originalPass)){
                return "0";
            }else if(user.getPassword().equals(newPass)){
                return "2";
            }else if(!(newPass.length()>=8 && newPass.length() <=12)){
                return "-1";
            }
            boolean b = userService.modifyPassword(user.getId(),newPass);
            if(b){
                session.removeAttribute("user");
                return "1";
            }
        }
        return "-1";
    }

    @RequestMapping("/success")
    public String toSuccess(){
        return "success";
    }

}
