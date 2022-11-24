package ru.mephi.gpus_agrgtr.parser.videocards.dns.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParamDTO {
    private String name;
    private String value;
}
