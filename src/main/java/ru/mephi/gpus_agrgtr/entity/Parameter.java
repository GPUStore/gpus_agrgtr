package ru.mephi.gpus_agrgtr.entity;

import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Data
@Setter
@Entity
@Table(name = "parameter")
public class Parameter {
    @Id
    @GeneratedValue(generator="system_uuid")
    @GenericGenerator(name="system_uuid", strategy = "uuid")
    @Column(name = "parameter_id", length = 32)
    private String id;

/*    @Column(name = "name")
    private String name;
    @Column(name = "value", length = 512)
    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

*//*    @ManyToOne(*//**//*cascade = CascadeType.ALL*//**//*)
    @JoinColumn(name = "characteristic_id", nullable = false)
    private Characteristic characteristic;*//*

    public Parameter setId(String id) {
        this.id = id;
        return this;
    }

    public Parameter setName(String name) {
        this.name = name;
        return this;
    }

    public Parameter setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return name.equals(parameter.name) && value.equals(parameter.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }*/
}
