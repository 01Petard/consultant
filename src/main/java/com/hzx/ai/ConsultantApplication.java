package com.hzx.ai;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.hzx.ai")
@Slf4j
public class ConsultantApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsultantApplication.class, args);
    }
}
