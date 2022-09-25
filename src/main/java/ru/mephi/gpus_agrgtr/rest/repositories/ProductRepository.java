package ru.mephi.gpus_agrgtr.rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.gpus_agrgtr.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
