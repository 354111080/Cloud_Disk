package com.eton.utils;

import com.eton.pojo.File;

import java.text.SimpleDateFormat;
import java.util.List;

public class FileUtil {

    public static String getType(String suffix){
        if(".png".equals(suffix) || ".jpg".equals(suffix) || ".gif".equals(suffix) || ".jpeg".equals(suffix) || ".bmp".equals(suffix) || ".webp".equals(suffix)){
            return "image";
        }else if(".txt".equals(suffix) || ".pdf".equals(suffix)){
            return "document";
        }else if(".mp4".equals(suffix) || ".wmv".equals(suffix)){
            return "video";
        }else if(".mp3".equals(suffix)){
            return "audio";
        }else {
            return "other";
        }
    }

    public static String getViewSize(long size) {
        if(size>=1024 && size<1048576){
            return (size/1024)+"KB";
        }else if(size > 1048576){
            return (size/1048576)+"MB";
        }else {
            return size+"B";
        }
    }

    public static String castFileList(List<File> list){
        if(list != null){
            String timeFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
            StringBuilder sb = new StringBuilder();
            for (File fileUnit:
                    list) {
                sb.append("<tr class='file-item files'>");
                sb.append("<td>");
                sb.append("<i style='font-size: 18px;' class='icon ion-android-list'>");
                sb.append("&nbsp;"+fileUnit.getName());
                sb.append("</i>");
                sb.append("</td>");
                sb.append("<td>");
                sb.append(fileUnit.getType());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(fileUnit.getSize());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(sdf.format(fileUnit.getCreate_time()));
                sb.append("</td>");
                sb.append("<td>");
                if(!"other".equals(fileUnit.getType())){
                    String realPath = fileUnit.getReal_path();
                    String fileRealName = realPath.substring(realPath.lastIndexOf("/"));
                    sb.append("<button class='btn btn-sm btn-success' value='http://129.211.164.89/files/"+fileUnit.getUser_id()+fileRealName+"' onclick='preview(this)'>Preview</button>");
                }
                sb.append("<button class='btn btn-sm btn-info' style='margin-left:3px;' value='"+fileUnit.getId()+"' onclick='download(this)'>Download</button>");
                sb.append("<button class='btn btn-sm btn-danger' style='margin-left:3px;' value='"+fileUnit.getId()+"' onclick='deleteFile(this)'>Delete</button>");
                sb.append("</td>");
                sb.append("</tr>");
            }
            return sb.toString();
        }
        return "";
    }
}
