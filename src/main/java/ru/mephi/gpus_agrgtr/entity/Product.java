package ru.mephi.gpus_agrgtr.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = "uuid")
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
    @Column(name = "weight")
    private double weight;
    @Column(name = "weight_with_box")
    private double weightWithBox;
    //@OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //private List<Parameter> parameters;
    @Enumerated(EnumType.STRING)
    private Type type;

    public Product setId(String id) {
        this.id = id;
        return this;
    }

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

    public Product setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public Product setWeightWithBox(double weightWithBox) {
        this.weightWithBox = weightWithBox;
        return this;
    }

    public Product setType(Type type) {
        this.type = type;
        return this;
    }
}
