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
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void save(List<Product> products) {
        for (Product product : products) {
            Optional<Product> optionalProduct = productRepository.findProductByName(product.getName());
            if (optionalProduct.isPresent()) {
                optionalProduct.get().getStore().addAll(product.getStore().stream().peek(store -> store.setProduct(optionalProduct.get())).toList());
                productRepository.save(optionalProduct.get());
            } else {
                product.getParameters().stream().forEach(parameter -> parameter.setProduct(product));
                product.getStore().stream().forEach(store -> store.setProduct(product));
                productRepository.save(product);
            }
        }
        System.out.println();
    }
}

