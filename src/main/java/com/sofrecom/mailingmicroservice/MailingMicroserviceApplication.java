package com.sofrecom.mailingmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MailingMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailingMicroserviceApplication.class, args);
    }

}
