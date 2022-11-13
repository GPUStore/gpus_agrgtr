package ru.mephi.gpus_agrgtr.rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.mephi.gpus_agrgtr.parser.Parser;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParseService {

    private final List<Parser> parsers;
    private final ProductService productService;

    @Scheduled(cron = "${agrgtr.cron:0 0 0 * * *}")
    public void startParse() {
        log.info("Парсинг начался");
        for (Parser parser : parsers) {
            log.info("Начался парсинг " + parser.getStoreName());
            productService.save(parser.parse());
        }
        log.info("Парсинг закончился");
    }
}
