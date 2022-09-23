package ru.mephi.gpus_agrgtr.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product")
@Data
public abstract class Product {

    @Id
    private String name;
    private String url;
    private Double cost;

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public Product setUrl(String url) {
        this.url = url;
        return this;
    }

    public Product setCost(Double cost) {
        this.cost = cost;
        return this;
    }
}
