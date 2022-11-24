package ru.mephi.gpus_agrgtr.parser.videocards.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Document;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.mephi.gpus_agrgtr.AbstractTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AbstractParserTest extends AbstractTest {

    protected ObjectMapper getObjectMapper() {
        return Jackson2ObjectMapperBuilder
                .json()
                .build()
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    protected Document getTestDocument(String fileName) throws IOException {
        Document document = new Document(fileName);
        document.append(Files.readString(Path.of(fileName)));
        return document;
    }

    protected String getTestJsonString(String fileName) throws IOException {
        return Files.readString(Path.of(fileName));
    }
}
