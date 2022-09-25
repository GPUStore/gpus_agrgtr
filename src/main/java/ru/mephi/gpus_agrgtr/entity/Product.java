package ru.mephi.gpus_agrgtr.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@EqualsAndHashCode
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(generator="system_uuid")
    @GenericGenerator(name="system_uuid", strategy = "uuid")
    @Column(name = "product_id", length = 32)
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "url")
    private String url;
    @Column(name = "cost")
    private Double cost;
    @Column(name = "country")
    private String country;

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

    public Product setCountry(String country) {
        this.country = country;
        return this;
    }
}
