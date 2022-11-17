package ru.mephi.gpus_agrgtr;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class GpusAgrgtrApplication{

    public static void main(String[] args) {
        SpringApplication.run(GpusAgrgtrApplication.class, args);
    }
}
