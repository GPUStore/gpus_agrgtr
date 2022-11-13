package ru.mephi.gpus_agrgtr.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.mephi.gpus_agrgtr.entity.Characteristic;
import ru.mephi.gpus_agrgtr.entity.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public abstract class Parser {
    protected String url;
    protected String storeName;
    protected ObjectMapper mapper;

    public Parser(String url, String storeName, ObjectMapper mapper) {
        this.url = url;
        this.storeName = storeName;
        this.mapper = mapper;
    }

    public abstract Characteristic toCharacteristic(String name);

    public List<Product> parse() {
        try {
            return getAllProducts();
        } catch (Exception e) {
            log.info("Page parsing failed: " + url + '\n' + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Document get(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException("Can not parse this page: " + url);
        }
    }

    public <T> T post(String requestLink, String requestBody, Class<T> classT) {
        try {
            String json = Jsoup.connect(requestLink)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .requestBody(requestBody)
                    .execute()
                    .body();
            return mapper.readValue(json, classT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract List<Product> getAllProducts() throws Exception;
}
