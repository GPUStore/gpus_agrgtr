package ru.mephi.gpus_agrgtr.parser.videocards.entity;

import lombok.Data;

import java.util.List;

@Data
public class SpecificationsDTO {
    String country;
    List<FullDTO> full;
    Double grossWeight;
    Double netWeight;
}
