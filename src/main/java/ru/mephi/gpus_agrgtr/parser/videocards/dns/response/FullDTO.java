package ru.mephi.gpus_agrgtr.parser.videocards.dns.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class FullDTO {
    private String name;
    private List<ParamDTO> list;
}
