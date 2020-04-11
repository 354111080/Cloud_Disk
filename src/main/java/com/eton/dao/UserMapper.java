package com.eton.dao;

import com.eton.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    /**
     * 通过Email查询用户
     * @param email
     * @return 用户的邮箱信息
     */
    String selectUserByEmail(String email);

    /**
     * 通过邮件验证用户
     * @param email
     * @return 返回的对象只包含密码和邮箱地址
     */
    User checkUserByEmail(String email);

    /**
     * 获取用户的所有信息，用于在主界面显示
     * @param email
     * @return 用户对象
     */
    User getUser(String email);

    /**
     * 添加新用户
     * @param user
     * @return 影响的行数
     */
    int addNewUser(User user);


    //查询用户头像
    String getUserProfilePic(Integer user_id);

    //添加头像
    int addProfilePic(User user);

    //修改用户名
    int updateUsername(User user);

    //修改用户密码
    int updatePassword(User user);

    int updateForgetPassword(User user);
}
