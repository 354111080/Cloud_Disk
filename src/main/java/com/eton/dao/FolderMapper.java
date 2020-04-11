package com.eton.dao;

import com.eton.pojo.Folder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FolderMapper {

    //新建文件加
    int addFolder(Folder folder);

    //获取用户文件路径信息
    List<Folder>  getFolders(Folder folder);

    //查询单个folder
    Folder getFolder(String id);

    //删除文件路径
    int deleteFolder(String id);

    //查询父路径下子路径的数量
    List<Folder> selectFoldersByFatherId(String father_id);

    //根据父路径删除子路径
    int deleteFolders(String father_id);
}
