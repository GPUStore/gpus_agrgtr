package ru.mephi.gpus_agrgtr.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "parameter")
public class Parameter {
    @Id
    @GeneratedValue(generator="system_uuid")
    @GenericGenerator(name="system_uuid", strategy = "uuid")
    @Column(name = "parameter_id", length = 32)
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "value", length = 512)
    private String value;

    public Parameter setName(String name) {
        this.name = name;
        return this;
    }

    public Parameter setValue(String value) {
        this.value = value;
        return this;
    }
}
