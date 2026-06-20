package com.gymcn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * GymCN 后端服务启动类（CloudBase 云函数版本）
 * 
 * 部署说明：
 * 1. 使用 mvn package 打包为 jar
 * 2. 在 CloudBase 控制台创建云函数
 * 3. 选择 Java 运行时，上传 jar 包
 * 4. 配置环境变量指向云 MySQL
 * 5. 设置触发器为 HTTP 触发
 */
@SpringBootApplication
public class GymcnApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymcnApplication.class, args);
    }
}
