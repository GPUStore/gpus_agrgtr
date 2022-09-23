package ru.mephi.gpus_agrgtr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.mephi.gpus_agrgtr.parser.technopark.TechnoparkParser;

@SpringBootApplication
public class GpusAgrgtrApplication implements CommandLineRunner {

    @Autowired
    TechnoparkParser technoparkParser;

    public static void main(String[] args) {
        SpringApplication.run(GpusAgrgtrApplication.class, args);
    }

    @Override
    public void run(String... args) {
        technoparkParser.getAllVideoCards();
    }
}
