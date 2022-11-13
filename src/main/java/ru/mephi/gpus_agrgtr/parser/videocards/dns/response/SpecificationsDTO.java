package ru.mephi.gpus_agrgtr.parser.videocards.dns.response;

import lombok.Data;
import java.util.List;

@Data
public class SpecificationsDTO {
    List<FullDTO> full;
    Double netWeight;
}
