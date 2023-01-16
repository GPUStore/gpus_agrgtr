package ru.mephi.gpus_agrgtr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
    public class WebClientConfig {
        @Value("${agrgtr.stat_server.url}")
        private String url;

        @Bean
        public WebClient getSenderWebClient() {
            return WebClient.builder()
                    .baseUrl(url)
                    .build();
        }

        @Bean
        public ObjectWriter getJsonWriter() {
            return new ObjectMapper()
                    .writer()
                    .withDefaultPrettyPrinter();
        }
    }
