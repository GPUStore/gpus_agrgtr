package ru.mephi.gpus_agrgtr.rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.EntityGraph;
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
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CharacteristicRepository characteristicRepository;
    private final ParameterRepository parameterRepository;

    //@Transactional
    public void save(List<Product> products) {
        for (Product product : products) {
/*            for (Parameter parameter : product.getParameters()) {
*//*                if (characteristicRepository.findCharacteristicByName(parameter.getCharacteristic().getName()).isEmpty()) {
                    characteristicRepository.save(parameter.getCharacteristic());
                }*//*
                //if (parameterRepository.findParameterByNameAndValue(parameter.getName(), parameter.getValue()).isEmpty())
                    parameterRepository.save(parameter);

            }*/
            productRepository.save(product);
        }

    }
}

