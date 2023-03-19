package com.lxy.rpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LxySpiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LxySpiApplication.class, args);
        try {
            System.in.read();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

