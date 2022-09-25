package ru.mephi.gpus_agrgtr.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode
@Entity
@Table(name = "characteristic")
public class Characteristic {
/*    @Id
    @GeneratedValue(generator="system_uuid")
    @GenericGenerator(name="system_uuid", strategy = "uuid")
    @Column(name = "characteristic_id", length = 32)
    private String id;*/

    @Id
    @Column(name = "characteristic_id")
    private String name;
    @OneToMany(mappedBy = "characteristic", cascade = CascadeType.ALL)
    private List<Parameter> parameters;

    @ManyToMany(mappedBy = "characteristics", fetch = FetchType.LAZY)
    private List<Product> products;

    public Characteristic setName(String name) {
        this.name = name;
        return this;
    }

    public Characteristic setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Characteristic setProducts(List<Product> products) {
        this.products = products;
        return this;
    }
}
