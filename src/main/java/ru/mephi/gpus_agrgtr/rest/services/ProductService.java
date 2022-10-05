package ru.mephi.gpus_agrgtr.rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.rest.repositories.ProductRepository;
import ru.mephi.gpus_agrgtr.rest.repositories.StoreRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void save(List<Product> products) {
        for (Product product : products) {
            Product prod = productRepository.findProductByName(product.getName()).orElse(null);
            if (null != prod) {
                //todo если видеокарта есть, то добавить к ней новый магазин
            } else {
                product.getParameters().forEach(parameter -> parameter.setProduct(product));
                product.getStores().forEach(store -> store.setProduct(product));
                productRepository.save(product);
            }
        }
        System.out.println();
    }
}

