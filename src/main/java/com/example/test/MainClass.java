package com.example.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@MapperScan("com.example.test.mapper")
@EnableScheduling
public class MainClass {
    public static void main(String[] args) {
        try {
            Logger julLogger = Logger.getLogger("org.apache.http");
            julLogger.setLevel(Level.OFF);
            SpringApplication.run(MainClass.class,args);
//            http://localhost:9000/swagger-ui/index.html openapi
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
