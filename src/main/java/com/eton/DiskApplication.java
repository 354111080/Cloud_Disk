package com.eton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

//开启异步配置
@EnableAsync
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class DiskApplication {

    public static void main(String[] args) {

        SpringApplication.run(DiskApplication.class, args);

    }

}
