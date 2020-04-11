package com.eton.service;

import com.eton.dao.FileMapper;
import com.eton.dao.FolderMapper;
import com.eton.pojo.File;
import com.eton.pojo.Folder;
import com.eton.utils.FtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FolderService {

    private FolderMapper folderMapper;

    @Autowired
    public void setFolderMapper(FolderMapper folderMapper) {
        this.folderMapper = folderMapper;
    }

    private FileMapper fileMapper;

    @Autowired
    public void setFileMapper(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    /**
     * 新建文件夹
     * @param folderName
     * @param userId
     * @return
     */
    public boolean createFolder(String id,String folderName,Integer userId,String father_dir,Date date){
        Folder folder = new Folder(userId,id,folderName,date,father_dir);
        int res = folderMapper.addFolder(folder);
        if(res>0){
            return true;
        }
        return false;
    }

    /**
     * 获取文件路径信息
     * @param userId
     * @param fatherDir
     * @return
     */
    public List<Folder> getFolders(Integer userId,String fatherDir){
        Folder folder = new Folder();
        folder.setUser_id(userId);
        folder.setFather_dir(fatherDir);
        List<Folder> list = folderMapper.getFolders(folder);
        return list;
    }

    /**
     * 根据路径id查询路径信息
     * @param folderId
     * @return
     */
    public Folder getFolder(String folderId){
        Folder folder = folderMapper.getFolder(folderId);
        return folder;
    }


    public boolean deleteFolder(String folderId){
        int res = folderMapper.deleteFolder(folderId);
        if(res>0){
            return true;
        }
        return false;
    }

    /**
     * 删除父路径下的文件
     * @param fatherId
     */
    public void deleteFiles(String fatherId,Integer userId){
        List<File> list = fileMapper.selectFilesByFatherId(fatherId);
        if(fileMapper.selectFilesByFatherId(fatherId).size()>0){//在父路径中含有文件的条件下执行删除语句
            fileMapper.deleteFilesByFatherId(fatherId);
            FtpUtil.deleteFiles(list,userId);
        }
    }

    /**
     * 删除子路径文件
     * @param fatherId
     */
    public void deleteFolders(String fatherId,Integer userId){
        List<Folder> list = folderMapper.selectFoldersByFatherId(fatherId);
        deleteFiles(fatherId,userId);
        if(list.size()>0){
            folderMapper.deleteFolders(fatherId);
            for (Folder folder:
                    list) {
                deleteFolders(folder.getId(),userId);//递归调用
            }
        }
    }

}
