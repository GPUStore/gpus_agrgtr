package ru.mephi.gpus_agrgtr.rest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import ru.mephi.gpus_agrgtr.rest.services.dto.ProductRsDto;

class MessageSenderTest {
    private String url ="http://localhost:8765/statistics" ;
    WebClient webClient = WebClient.builder().baseUrl(url).build();
    ObjectWriter objectWriter =  new ObjectMapper()
            .writer()
            .withDefaultPrettyPrinter();

    @Test
    void sendMessage() throws JsonProcessingException {
        MessageSender messageSender = new MessageSender(webClient,objectWriter);
        messageSender.sendMessage(new ProductRsDto("1", 1, 1));
    }
}