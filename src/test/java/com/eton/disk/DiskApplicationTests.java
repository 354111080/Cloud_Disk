package com.eton.disk;

import com.eton.dao.FolderMapper;
import com.eton.dao.UserMapper;
import com.eton.pojo.User;
import com.eton.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
class DiskApplicationTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    @Autowired
    FolderMapper folderMapper;

    @Test
    void contextLoads() {
        System.out.println(folderMapper.getFolder("1402652175-5994-4cfa-ad77-07a2dc3bf7f9"));
    }

}
