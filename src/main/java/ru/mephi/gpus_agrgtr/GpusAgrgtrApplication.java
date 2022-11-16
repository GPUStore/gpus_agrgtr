package ru.mephi.gpus_agrgtr;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.mephi.gpus_agrgtr.parser.Parser;
import ru.mephi.gpus_agrgtr.rest.services.ProductService;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class GpusAgrgtrApplication implements CommandLineRunner {

    private final List<Parser> parsers;
    private final ProductService productService;

    public static void main(String[] args) {
        SpringApplication.run(GpusAgrgtrApplication.class, args);
    }

    @Override
    public void run(String... args) {
        for (Parser parser : parsers) {
            parser.parse().forEach(productService::save);
        }
        System.out.println("THE END!");
    }
}
