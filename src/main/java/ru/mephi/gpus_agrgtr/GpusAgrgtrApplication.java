package ru.mephi.gpus_agrgtr;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
public class GpusAgrgtrApplication{

    public static void main(String[] args) {
        SpringApplication.run(GpusAgrgtrApplication.class, args);
    }
}
