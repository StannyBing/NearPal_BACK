package com.stanny.nearpal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.stanny.nearpal.mapper")
@ComponentScan(basePackages = {"com.stanny.nearpal.*"})
@SpringBootApplication
public class NearpalApplication {

    public static void main(String[] args) {
        SpringApplication.run(NearpalApplication.class, args);
    }

}
