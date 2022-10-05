package ru.mephi.gpus_agrgtr.parser.videocards.technopark.response;

import lombok.Data;

import java.util.List;

@Data
public class FullDTO {
    private String name;
    private List<ParamDTO> list;
}
