package ru.mephi.gpus_agrgtr.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.mephi.gpus_agrgtr.entity.Product;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class Parser {
    protected String url;
    protected ObjectMapper mapper;

    public Parser(String url, ObjectMapper mapper) {
        this.url = url;
        this.mapper = mapper;
    }

    public List<Product> parse() {
        try {
            return getAllProducts();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Document get(String url){
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            return new Document("");
        }
    }

    protected <T> T post(String requestLink, String requestBody, Class<T> classT){
        try {
            String json = Jsoup.connect(requestLink)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .requestBody(requestBody)
                    .execute()
                    .body();
            return mapper.readValue(json, classT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract List<Product> getAllProducts() throws Exception;
}
