package com.eton.controller;

import com.eton.pojo.File;
import com.eton.pojo.Folder;
import com.eton.pojo.User;
import com.eton.service.FileService;
import com.eton.service.FolderService;
import com.eton.utils.FileUtil;
import com.eton.utils.FtpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MainController {

    private FolderService folderService;

    @Autowired
    public void setFolderService(FolderService folderService) {
        this.folderService = folderService;
    }

    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping("/getUserInfo")
    @ResponseBody
    public String toService(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        String userJsonStr = null;
        if(user != null){
            User userForDisplay = new User(user.getEmail(),user.getUsername(),user.getProfile_pic());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                userJsonStr = objectMapper.writeValueAsString(userForDisplay);//将用户对象转换为Json对象
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return userJsonStr;
    }


    @GetMapping("/logout")
    @ResponseBody
    public void logOut(HttpServletRequest request, HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie emailCookie = new Cookie("email",null);
        Cookie passwordCookie = new Cookie("password",null);
        response.addCookie(emailCookie);
        response.addCookie(passwordCookie);
        try {
            response.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadFile")
    @ResponseBody
    public String uploadFile(HttpServletRequest request) throws FileUploadException {
        if(!ServletFileUpload.isMultipartContent(request)){
            return "-1";//说明这是一个普通的表单
        }
        DiskFileItemFactory factory=new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1073741824);
        HttpSession session = request.getSession();
        ProgressListener progressListener = new ProgressListener() {
            @Override
            public void update(long l, long l1, int i) {
                Long percent = l*80/l1;
                session.setAttribute("uploadProgress",percent);
            }
        };
        upload.setProgressListener(progressListener);
        List<FileItem> list = upload.parseRequest(request);

        FileItem file = list.get(0);

        long fileSize = file.getSize();
        if(fileSize<1l){
            return "不可以上传空文件！";
        }
        User user = (User) request.getSession().getAttribute("user");
        if(user==null){
            return "0";
        }
        Long usage = fileService.getUsage(user.getId());
        if(usage == null){
            usage = 0l;
        }
        if((fileSize+usage)>1073741824l){
            return "-2";//超出用户存储大小
        }

        //获取上传的文件名
        String originalFilename = file.getName();
        //获取文件类型
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID防止文件名重复
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + suffix;
        //获取用户信息
        Integer userId = user.getId();
        String dir = (String) session.getAttribute("dir");
        boolean flag = FtpUtil.uploadFile(dir,uuid,fileName,file,fileSize,userId,originalFilename.substring(originalFilename.lastIndexOf("\\")+1),suffix,fileService,request);
        if(flag){
            return "1";//上传成功
        }
        return "-1";//上传失败
    }

    /**
     * 把文件上传进度返回到前端
     * @param request
     * @return
     */
    @GetMapping("/getUploadProgress")
    @ResponseBody
    public String passUploadProgress(HttpServletRequest request){
        HttpSession session = request.getSession();
        if(session.getAttribute("uploadProgress")!=null){
            long rate = (long) session.getAttribute("uploadProgress");
            if(session.getAttribute("transferProgress")!=null){
                rate += (long)session.getAttribute("transferProgress");
            }
            if(rate == 100l){
                session.removeAttribute("uploadProgress");
                session.removeAttribute("transferProgress");
            }
            return rate+"";//返回进度
        }
        return "0";
    }

    @ResponseBody
    @GetMapping("/getFiles")
    public String viewFiles(HttpServletRequest request){
        HttpSession session = request.getSession();
        User currentUser = (User)session.getAttribute("user");
        if(currentUser != null){
            File file = new File();
            file.setUser_id(currentUser.getId());
            file.setFather_dir((String)session.getAttribute("dir"));//获取当前路径
            List<File> list = fileService.getFiles(file);
            return FileUtil.castFileList(list);
        }

        return "";

    }


    @GetMapping("/deleteFile")
    @ResponseBody
    public String deleteFile(String fileId){
        String realPath = fileService.getFileRealPath(fileId);
        if(realPath != null){
            if(FtpUtil.deleteFile(fileId,realPath,fileService)){
                return "1";
            }
        }
        return "0";
    }


    @GetMapping("/downLoadFile")
    @ResponseBody
    public void downloadFile(String fileId,HttpServletResponse response) throws IOException {
        File file = fileService.getFil(fileId);
        if(file != null){
            String realPath = file.getReal_path();
            if(realPath != null){
                String realFileName = realPath.substring(realPath.lastIndexOf("/")+1);
                realPath = realPath.substring(0,realPath.lastIndexOf("/")+1);
                FtpUtil.downloadFile(realFileName,realPath,response,file.getName());
            }
        }
    }

    @ResponseBody
    @GetMapping("/getFolders")
    public String viewFolders(@RequestParam("father_id") String dir,HttpServletRequest request){
        User currentUser = (User)request.getSession().getAttribute("user");
        if(currentUser != null){
            if(dir == null || dir.length() == 0){
                dir = currentUser.getId().toString();//如果Dir为空，则为根路径
            }
            List<Folder> list = folderService.getFolders(currentUser.getId(),dir);
            if(list != null){
                String timeFormat = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
                StringBuilder sb = new StringBuilder();
                for (Folder folderUnit:
                        list) {
                    sb.append("<tr class='file-item folders'>");
                    sb.append("<td>");
                    sb.append("<i style='font-size: 20px;color: #FFB90F;' class='icon ion-android-folder'>");
                    sb.append("&nbsp;"+folderUnit.getName());
                    sb.append("</i>");
                    sb.append("</td>");
                    sb.append("<td>folder");
                    sb.append("</td>");
                    sb.append("<td>-");
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(sdf.format(folderUnit.getCreate_time()));
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append("<button class='btn btn-sm btn-success' value='"+folderUnit.getId()+"' onclick='enterDir(this)'>Enter</button>");
                    sb.append("<button class='btn btn-sm btn-danger' style='margin-left:3px;' value='"+folderUnit.getId()+"' onclick='deleteDir(this)'>Delete</button>");
                    sb.append("</td>");
                    sb.append("</tr>");
                }
                return sb.toString();
            }
        }

        return "0";

    }

    @GetMapping("/createFolder")
    @ResponseBody
    public String createFolder(String folderName,HttpServletRequest request){
        if(folderName != null && folderName.length()>0 && folderName.length()<=12){//输入信息判断限制
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if(user != null){
                String father_dir = (String) session.getAttribute("dir");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String id = user.getId()+""+UUID.randomUUID();
                boolean b = folderService.createFolder(id,folderName,user.getId(),father_dir,date);
                if(b){
                    StringBuilder sb = new StringBuilder();
                    sb.append("<td>");
                    sb.append("<i style='color:#FFB90F;font-size: 20px;' class='icon ion-android-folder'>");
                    sb.append("&nbsp;"+folderName);
                    sb.append("</i>");
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append("folder");
                    sb.append("</td>");
                    sb.append("<td>-");
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(sdf.format(date));
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append("<button class='btn btn-sm btn-success' value='"+id+"' onclick='enterDir(this)'>Enter</button>");
                    sb.append("<button class='btn btn-sm btn-danger' style='margin-left:3px;' value='"+id+"' onclick='deleteDir(this)'>Delete</button>");
                    sb.append("</td>");
                    return sb.toString();//新建路径成功
                }
            }
        }
        return "0";//新建路径失败
    }


    @GetMapping("/enterDir")
    @ResponseBody
    public String enterDir(@RequestParam("folderId") String folderId,HttpServletRequest request){
        Folder folder = folderService.getFolder(folderId);
        if(folder != null){
            request.getSession().setAttribute("dir",folderId);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String folderJson = objectMapper.writeValueAsString(folder);
                return folderJson;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;//进入失败
    }


    @GetMapping("/skipDir")
    @ResponseBody
    public String skipDir(@RequestParam("folderId") String folderId,HttpServletRequest request){
        if(folderId!=null){
            HttpSession session = request.getSession();
            if("root".equals(folderId)){
                User user = (User) session.getAttribute("user");
                if(user == null){
                    return "0";
                }
                session.setAttribute("dir",user.getId().toString());
                return "1";//进入根路径成功
            }
            request.getSession().setAttribute("dir",folderId);
            return "1";//进入成功
        }
        return "0";//进入失败
    }



    @GetMapping("/deleteDir")
    @ResponseBody
    public String deleteDir(String folderId,HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user!=null){
            folderService.deleteFolders(folderId,user.getId());
            boolean b = folderService.deleteFolder(folderId);
            if(b){
                String currentDir = (String) session.getAttribute("dir");
                if(currentDir.equals(user.getId())){
                    return "";
                }
                return currentDir;
            }
        }
        return "-1";
    }


    @ResponseBody
    @GetMapping("/getFilesByType")
    public String getFileByType(HttpServletRequest request,String type){
        HttpSession session = request.getSession();
        User currentUser = (User)session.getAttribute("user");
        if(currentUser != null){
            File file = new File();
            file.setUser_id(currentUser.getId());
            file.setType(type);
            List<File> list = fileService.getFilesByType(file);
            return FileUtil.castFileList(list);
        }
        return "";

    }


    @GetMapping("/searchFileByKey")
    @ResponseBody
    public String getFileByKey(String key,HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user != null){
            List<File> list = fileService.getFilesByKey(user.getId(),key);
            return FileUtil.castFileList(list);
        }
        return "";
    }


    @GetMapping("/getUsage")
    @ResponseBody
    public String getUsage(HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user != null){
            Long usage = fileService.getUsage(user.getId());
            if(usage == null){
                return "0";
            }else {
                Long progress = usage;
                return progress.toString();
            }

        }
        return "-1";//获取失败
    }
}
