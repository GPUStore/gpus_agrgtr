package ru.mephi.gpus_agrgtr.parser.technopark.entity;

import lombok.Data;

import java.util.List;

@Data
public class FullDTO {
    private String name;
    private List<ParamDTO> list;
    private String __typename;
}
