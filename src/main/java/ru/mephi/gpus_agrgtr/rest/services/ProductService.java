package ru.mephi.gpus_agrgtr.rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.gpus_agrgtr.category.CategoryExtractor;
import ru.mephi.gpus_agrgtr.entity.Category;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.rest.repositories.ProductRepository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryExtractor categoryExtractor;

    @Transactional
    public void save(List<Product> products) {
        for (Product product : products) {
            populate(product);
            Optional<Product> prod = find(product.getName());
            if (prod.isPresent()) {
                addStores(product, prod.get());
                productRepository.save(prod.get());
            } else {
                productRepository.save(product);
            }
        }
        System.out.println();
    }

    public void populate(Product product) {
        Set<Category> categorySet = getCategories(product);
        categorySet.forEach(category -> category.getProducts().add(product));
        product.setCategories(categorySet)
                .getParameters()
                .forEach(parameter -> parameter.setProduct(product));
        product.getStores()
                .forEach(store -> {
                    store.setProduct(product);
                    store.setDate(Date.from(Instant.now()));
                });
    }

    public Optional<Product> find(String productName) {
        Optional<Product> prod = productRepository.findProductByName(productName);
        if (prod.isPresent()) {
            return prod;
        }
        prod = findProductByCategories(categoryExtractor.extractCategorySet(productName));
        if (prod.isPresent()) {
            log.info("found product by categories:" + productName + "=" + prod.get().getName() + "\n");
            return prod;
        }
        return Optional.empty();
    }

    private void addStores(Product newProduct, Product oldProduct) {
        newProduct.getStores()
                .forEach(store -> store.setProduct(oldProduct));
        oldProduct.getStores().addAll(newProduct.getStores());
    }

    public Set<Category> getCategories(Product product) {
        return categoryExtractor.extractCategorySet(product.getName());
    }

    public Optional<Product> findProductByCategories(Set<Category> categories) {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .filter(p -> categoryExtractor.isEqual(getCategories(p), categories))
                .findFirst();
    }
}


