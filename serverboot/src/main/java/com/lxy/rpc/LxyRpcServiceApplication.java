package com.lxy.rpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LxyRpcServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LxyRpcServiceApplication.class, args);
        try {
            System.in.read();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
