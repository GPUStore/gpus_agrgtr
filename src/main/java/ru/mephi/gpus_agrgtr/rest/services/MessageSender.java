package ru.mephi.gpus_agrgtr.rest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.mephi.gpus_agrgtr.rest.services.dto.ProductRsDto;


@Component
@RequiredArgsConstructor
public class MessageSender {
    private final WebClient webClient;
    private final ObjectWriter objectWriter;


    public void sendMessage(ProductRsDto dto) throws JsonProcessingException {
        webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/statistics/collect_and_send_updates")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(objectWriter.writeValueAsString(dto)))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
