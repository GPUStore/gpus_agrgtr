package ru.mephi.gpus_agrgtr.rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.gpus_agrgtr.category.CategoryExtractor;
import ru.mephi.gpus_agrgtr.entity.Category;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.entity.Store;
import ru.mephi.gpus_agrgtr.rest.repositories.ProductRepository;

import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryExtractor categoryExtractor;

    @Transactional
    public void save(Product product) {
        populate(product);
        Optional<Product> foundProduct = find(product.getName());
        if (foundProduct.isPresent()) {
            addStores(product, foundProduct.get());
            productRepository.save(foundProduct.get());
        } else {
            productRepository.save(product);
        }
    }

    private void populate(Product product) {
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

    private Optional<Product> find(String productName) {
        return productRepository
                .findProductByName(productName)
                .or(findProductByCategories(categoryExtractor.extractCategorySet(productName)));
    }

    private void addStores(Product newProduct, Product oldProduct) {
        Map<String, Double> eachStoreLastPriceGroupedByName = oldProduct
                .getStores()
                .stream()
                .collect(Collectors.groupingBy(Store::getName))
                .entrySet()
                .stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()
                                .stream()
                                .reduce((s1, s2) -> s1.getDate().compareTo(s2.getDate()) > 0 ? s1 : s2)
                                .orElseThrow(() -> new IllegalArgumentException("No date in store!"))
                                .getCost()
                ));
        newProduct.getStores()
                .forEach(store -> {
                    double newStorePrice = store.getCost();
                    Double oldStoreLastPrice = eachStoreLastPriceGroupedByName.get(store.getName());
                    if (oldStoreLastPrice != null && !oldStoreLastPrice.equals(newStorePrice)) {
                        store.setProduct(oldProduct);
                        oldProduct.getStores().add(store);
                    }
                });
    }

    public Set<Category> getCategories(Product product) {
        return categoryExtractor.extractCategorySet(product.getName());
    }

    public Supplier<? extends Optional<? extends Product>> findProductByCategories(Set<Category> categories) {
        List<Product> products = productRepository.findAll();
        return () ->
                products.stream()
                        .filter(p -> categoryExtractor.isEqual(getCategories(p), categories))
                        .findFirst();
    }
}


