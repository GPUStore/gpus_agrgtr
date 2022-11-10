package ru.mephi.gpus_agrgtr.rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mephi.gpus_agrgtr.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
    Boolean existsByName(String name);
}
