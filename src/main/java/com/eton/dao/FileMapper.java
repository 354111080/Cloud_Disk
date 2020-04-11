package com.eton.dao;

import com.eton.pojo.File;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FileMapper {

    //添加新文件
    int addFile(File file);

    //查询文件
    List<File> selectFiles(File file);

    //根据Id获取文件对象
    File getFile(String fileId);

    //获取文件的真是路径
    String getRealPath(String fileId);

    //删除文件
    int deleteFile(String fileId);

    //删除路径内文件
    int deleteFilesByFatherId(String father_dir);

    //查询父路径下的文件数量
    List<File> selectFilesByFatherId(String father_dir);

    //根据文件类型查询文件
    List<File> getFileByType(File file);

    //根据文件类型查询文件
    List<File> getFileByKey(Map<String,Object> map);

    //查询用户使用量
    Long getUsage(Integer user_id);
}
