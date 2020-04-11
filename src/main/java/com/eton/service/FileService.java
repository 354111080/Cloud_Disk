package com.eton.service;

import com.eton.dao.FileMapper;
import com.eton.pojo.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileService {

    private FileMapper fileMapper;

    @Autowired
    public void setFileMapper(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    /**
     * 添加文件信息到数据库
     * @param file
     * @return true：插入成功 false：插入失败
     */
    public boolean addFile(File file){
        int res = fileMapper.addFile(file);
        if(res>0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 按需求查询文件
     * @param file
     * @return
     */
    public List<File> getFiles(File file){
        return fileMapper.selectFiles(file);
    }


    public boolean deleteFile(String fileId){
        int res = fileMapper.deleteFile(fileId);
        if(res>0){
            return true;
        }
        return false;
    }

    /**
     *根据文件Id获取文件对象
     * @param fileId
     * @return
     */
    public File getFil(String fileId){
        return fileMapper.getFile(fileId);
    }

    /**
     * 根据文件ID获取文件真实路径
     * @param fileId
     * @return
     */
    public String getFileRealPath(String fileId){
        return fileMapper.getRealPath(fileId);
    }

    /**
     * 根据文件类型获取文件
     * @param file
     * @return
     */
    public List<File> getFilesByType(File file){
        return fileMapper.getFileByType(file);
    }


    public List<File> getFilesByKey(Integer userId,String key){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("user_id",userId);
        map.put("key","%"+key+"%");
        return fileMapper.getFileByKey(map);
    }

    public Long getUsage(Integer userId){
        return fileMapper.getUsage(userId);
    }
}
