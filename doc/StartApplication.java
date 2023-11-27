package com.lottery;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan({"com.lottery","com.villa"})
@ComponentScan({"com.lottery","com.villa"})//指定扫描其他包
@ServletComponentScan({"com.lottery","com.villa"})//通过@WebServlet、@WebFilter、@WebListener注解自动注册
@EnableScheduling//开启任务调度
public class StartApplication {
    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }
}