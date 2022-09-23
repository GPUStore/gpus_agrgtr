package ru.mephi.gpus_agrgtr.entity;

import lombok.Data;
import lombok.Setter;

import java.util.Map;

@Data
@Setter
public class Videocard extends Product {
    private String country;
    private double weight;
    private double weightWithBox;
    private Map<String, Map<String, String>> characteristics;

    public Videocard setCountry(String country) {
        this.country = country;
        return this;
    }

    public Videocard setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public Videocard setWeightWithBox(double weightWithBox) {
        this.weightWithBox = weightWithBox;
        return this;
    }

    public Videocard setCharacteristics(Map<String, Map<String, String>> characteristics) {
        this.characteristics = characteristics;
        return this;
    }
}
