package ru.mephi.gpus_agrgtr.rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.gpus_agrgtr.category.CategoryExtractor;
import ru.mephi.gpus_agrgtr.entity.Category;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.rest.repositories.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final CategoryExtractor categoryExtractor;

    @Transactional
    public void save(List<Product> products) {
        for (Product product : products) {
            Product prod = productRepository.findProductByName(product.getName()).orElse(null);
            if (prod == null) {
                prod = findProductByCategories(getCategories(product)).orElse(null);
                if (prod != null) {
                    log.info("found product by categories:" + product.getName() + "=" + prod.getName());
                }
            }
            if (prod == null) {
                Set<Category> categorySet = getCategories(product);
                categorySet.forEach(category -> category.getProducts().add(product));
                product.setCategories(categorySet)
                        .getParameters()
                        .forEach(parameter -> parameter.setProduct(product));
                product.getStores()
                        .forEach(store -> store.setProduct(product));
                categoryService.save(categorySet);

                productRepository.save(product);
            }
        }
        System.out.println();
    }

    public Set<Category> getCategories(Product product) {
        Optional<Product> productFromDb = productRepository.findProductByName(product.getName());
        if (productFromDb.isPresent())
            return productFromDb.get().getCategories();
        return categoryExtractor.extractCategorySet(product.getName());
    }

    public Optional<Product> findProductByCategories(Set<Category> categories) {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .filter(p -> categoryExtractor.isEqual(getCategories(p), categories))
                .findFirst();
    }
}

