package ru.mephi.gpus_agrgtr.parser.videocards;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.mephi.gpus_agrgtr.parser.videocards.technopark.TechnoparkParser;

class TechnoparkParserTest {

    TechnoparkParser technoparkParser;

    @BeforeEach
    public void init() {
        technoparkParser = new TechnoparkParser(
                "",
                "",
                "",
                getObjectMapper()
        );
    }

    private ObjectMapper getObjectMapper() {
        return Jackson2ObjectMapperBuilder
                .json()
                .build()
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    @Test
    void getAllProducts() {
    }
}