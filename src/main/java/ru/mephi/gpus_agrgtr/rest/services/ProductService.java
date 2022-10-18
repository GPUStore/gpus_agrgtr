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

            if (prod == null) {
                prod = categoryService.findProductByCategories(product);
                if (prod != null) {
                    log.info("found product by categories:" + product.getName() + "=" + prod.getName());
                }
            }
            if (prod == null) {
                Set<Category> categorySet = categoryService.getCategories(product);
                categorySet.forEach(category -> category.addProduct(product));
                product.addCategories(categorySet)
                        .getParameters().forEach(parameter -> parameter.setProduct(product));
                product.getStores().forEach(store -> store.setProduct(product));
                categoryService.saveInRepository(categorySet);
                productRepository.save(product);
            }
        }
        System.out.println();
    }
}

