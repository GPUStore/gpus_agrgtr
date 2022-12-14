package ru.mephi.gpus_agrgtr.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = "uuid")
    @Column(name = "product_id", length = 32)
    private String id;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Store> stores;
    @Column(name = "country")
    private String country;
    @Column(name = "weight")
    private double weight;
    @Column(name = "weight_with_box")
    private double weightWithBox;
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Parameter> parameters;
    @Enumerated(EnumType.STRING)
    private Type type;
    @ManyToMany(cascade =
            {
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.PERSIST
            })
    @JoinTable(
            name = "products_categories",
            inverseJoinColumns = @JoinColumn(
                    name = "category_id", referencedColumnName = "category_id"),
            joinColumns = @JoinColumn(
                    name = "product_id", referencedColumnName = "product_id")

    )
    private Set<Category> categories;

    public Product setStores(List<Store> store) {
        this.stores = store;
        return this;
    }

    public Product setCategories(Set<Category> categorySet) {
        this.categories = categorySet;
        return this;
    }

    public Product setId(String id) {
        this.id = id;
        return this;
    }

    public Product setName(String name) {
        this.name = name;
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

    public Product setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Product setType(Type type) {
        this.type = type;
        return this;
    }
}
