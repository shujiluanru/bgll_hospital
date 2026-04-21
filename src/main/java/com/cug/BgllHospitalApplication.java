package com.cug;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cug.mapper")
public class BgllHospitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(BgllHospitalApplication.class, args);
    }

}
