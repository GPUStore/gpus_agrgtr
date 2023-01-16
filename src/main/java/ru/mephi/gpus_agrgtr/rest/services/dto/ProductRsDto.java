package ru.mephi.gpus_agrgtr.rest.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRsDto {
    private String productId;
    private double oldCost;
    private double newCost;
    }
