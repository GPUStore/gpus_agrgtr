package ru.mephi.gpus_agrgtr.rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.gpus_agrgtr.entity.Category;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.rest.repositories.ProductRepository;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryService categoryService;


    @Transactional
    public void save(List<Product> products) {
        for (Product product : products) {
            Product prod = productRepository.findProductByName(product.getName()).orElse(null);
            if (prod == null){
                prod = categoryService.findProductByCategories(product);
                if(prod != null){
                    Logger.getGlobal().log(Level.ALL, "found product by categories");
                }
            }
            if (prod == null) {
                Set<Category> categorySet = categoryService.getCategories(product);
                categorySet.forEach(category -> category.addProduct(product));
                product.addCategories(categorySet);
                product.getParameters().forEach(parameter -> parameter.setProduct(product));
                product.getStores().forEach(store -> store.setProduct(product));
                categoryService.saveInRepository(categorySet);
                productRepository.save(product);
            }
            //prod.addStore(product.getStore)
        }
        System.out.println();
    }
}

