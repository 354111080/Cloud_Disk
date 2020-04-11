package com.eton.utils;

import com.eton.pojo.File;
import com.eton.service.FileService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.apache.commons.net.io.CopyStreamEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class FtpUtil {

    //ftp服务器ip地址
    private static final String FTP_ADDRESS = "10.206.0.3";
    //端口号
    private static final int FTP_PORT = 21;
    //用户名
    private static final String FTP_USERNAME = "file-admin";
    //密码
    private static final String FTP_PASSWORD = "080411as";
    //附件路径
    public static final String FTP_BASEPATH = "/home/file-admin/";

    public static FTPClient getFTPClient(){

        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(FTP_ADDRESS,FTP_PORT);
            ftp.login(FTP_USERNAME,FTP_PASSWORD);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftp;

    }

    //删除文件
    public static boolean deleteFile(String fileId, String realPath,FileService fileService){
        FTPClient ftp = getFTPClient();
        int reply;
        boolean flag = false;
        reply = ftp.getReplyCode();//连接状态码
        ftp.enterLocalPassiveMode();
        try {
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                return false;
            }
            String dir = realPath.substring(0,realPath.lastIndexOf("/"));
            ftp.makeDirectory(dir);
            ftp.changeWorkingDirectory(dir);
            if(fileService.deleteFile(fileId)){
                String fileName = realPath.substring(realPath.lastIndexOf("/")+1);
                ftp.deleteFile(fileName);
                flag = true;
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 文件上传
     * @param dir
     * @param uuid
     * @param fileName
     * @param file
     * @param fileSize
     * @param userId
     * @param originalFilename
     * @param suffix
     * @param fileService
     * @param request
     * @return
     */
    public static boolean uploadFile(String dir, UUID uuid, String fileName, FileItem file, long fileSize, Integer userId, String originalFilename, String suffix, FileService fileService, HttpServletRequest request){
        boolean flag = false;
        FTPClient ftp = getFTPClient();

        int reply;
        reply = ftp.getReplyCode();//连接状态码

        ftp.enterLocalPassiveMode();
        try {
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                return false;
            }
            ftp.setControlEncoding("UTF-8");
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);//设置文件类型为二进制
            ftp.makeDirectory(FTP_BASEPATH+userId);
            ftp.changeWorkingDirectory(FTP_BASEPATH+userId);
            HttpSession session = request.getSession();
            //为上传ftp文件进度添加监听
            InputStream inputStream = file.getInputStream();
            CopyStreamAdapter streamListener = new CopyStreamAdapter() {

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {

                    Long percent = totalBytesTransferred*20/fileSize;
                    session.setAttribute("transferProgress",percent);
                    session.setAttribute("fileUploaded",totalBytesTransferred);
                }

                @Override
                public void bytesTransferred(CopyStreamEvent event) {
                    super.bytesTransferred(event);
                }
            };
            ftp.setCopyStreamListener(streamListener);

            ftp.setBufferSize(1024*8);//设置缓存区，8k

            ftp.storeFile(fileName,inputStream);
            inputStream.close();
            if((long)session.getAttribute("fileUploaded") != fileSize){
                ftp.deleteFile(FTP_BASEPATH+userId+"/"+fileName);
                session.removeAttribute("fileUploaded");
                ftp.logout();
                return false;//上传中断
            }
            session.removeAttribute("fileUploaded");
            //创建文件对象
            File newFile = new File(userId,userId+":"+uuid,originalFilename, FileUtil.getType(suffix),FileUtil.getViewSize(fileSize),new Date(),dir,FtpUtil.FTP_BASEPATH+userId+"/"+fileName,fileSize);
            boolean b = fileService.addFile(newFile);
            if(!b){
                ftp.deleteFile(FTP_BASEPATH+userId+"/"+fileName);
                ftp.logout();
                return false;//插入失败
            }
            ftp.logout();
            flag = true;
        } catch (IOException e) {
            try {
                ftp.logout();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return flag;

    }

    /**
     * 文件下载
     * @param fileName
     * @param realPath
     * @return
     */
    public static void downloadFile(String realFileName, String realPath, HttpServletResponse response,String fileName){
        FTPClient ftp = getFTPClient();
        InputStream is = null;
        ServletOutputStream out = null;
        int reply;
        boolean flag = false;
        reply = ftp.getReplyCode();//连接状态码
        ftp.enterLocalPassiveMode();
        try {
            ftp.enterLocalPassiveMode();
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
            }
            ftp.setBufferSize(1024*8);
            is = ftp.retrieveFileStream(realPath+realFileName);// 获取ftp上的文件
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileName,"UTF-8"));//设置下载响应
            out = response.getOutputStream();//获取响应输出流
            //建立缓冲区
            byte[] buffer = new byte[1024*8];
            int len = 0;//读取的字节数
            while((len=is.read(buffer))>0){
                out.write(buffer,0,len);
            }
            is.close();
            out.flush();
            out.close();
            ftp.logout();
        } catch (IOException e) {
            try {
                if(is!=null){
                    is.close();
                }
                if(out != null){
                    out.close();
                }
                ftp.logout();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    /**
     * 删除父路径下的所有文件
     * @param list
     * @param userId
     */
    public static void deleteFiles(List<File> list,Integer userId){
        FTPClient ftp = getFTPClient();
        int reply;
        boolean flag = false;
        reply = ftp.getReplyCode();//连接状态码
        ftp.enterLocalPassiveMode();
        try {
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
            }
            String dir = FTP_BASEPATH+userId;
            ftp.makeDirectory(dir);
            ftp.changeWorkingDirectory(dir);
            for (File file:
                 list) {
                String path = file.getReal_path();
                ftp.deleteFile(path.substring(path.lastIndexOf("/")+1));
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //上传头像
    public static boolean upLoadProfilePic(Integer userId,InputStream inputStream,String fileName){
        FTPClient ftp = getFTPClient();
        int reply;
        boolean flag = false;
        reply = ftp.getReplyCode();//连接状态码
        ftp.enterLocalPassiveMode();
        try {
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                return false;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);//设置文件类型为二进制
            String dir = FTP_BASEPATH+userId+"_profilePic";
            ftp.makeDirectory(dir);
            ftp.changeWorkingDirectory(dir);
            ftp.storeFile(fileName,inputStream);
            flag = true;
            inputStream.close();
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


}
