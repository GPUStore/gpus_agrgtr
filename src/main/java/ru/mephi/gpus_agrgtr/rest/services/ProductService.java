package ru.mephi.gpus_agrgtr.rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.gpus_agrgtr.entity.Characteristic;
import ru.mephi.gpus_agrgtr.entity.Parameter;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.rest.repositories.CharacteristicRepository;
import ru.mephi.gpus_agrgtr.rest.repositories.ParameterRepository;
import ru.mephi.gpus_agrgtr.rest.repositories.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CharacteristicRepository characteristicRepository;
    private final ParameterRepository parameterRepository;

    public void save(List<Product> products) {}

}

