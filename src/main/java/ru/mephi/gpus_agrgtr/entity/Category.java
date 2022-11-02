package ru.mephi.gpus_agrgtr.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "category")
public class Category {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int category_id;

    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinTable(
            name = "products_categories",
            inverseJoinColumns = @JoinColumn(
                    name = "product_id", referencedColumnName = "product_id"),
            joinColumns  = @JoinColumn(
                    name = "category_id", referencedColumnName = "category_id")

    )
    private List<Product> products;

    public Category() {
        products = new ArrayList<>();
    }

    public Category(String name) {
        this.name = name;
        products = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return name.equals(category.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
