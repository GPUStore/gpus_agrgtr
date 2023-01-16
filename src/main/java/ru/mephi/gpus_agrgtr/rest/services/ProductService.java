package ru.mephi.gpus_agrgtr.rest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.gpus_agrgtr.category.CategoryExtractor;
import ru.mephi.gpus_agrgtr.entity.Category;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.entity.Store;
import ru.mephi.gpus_agrgtr.rest.repositories.ProductRepository;
import ru.mephi.gpus_agrgtr.rest.services.dto.ProductRsDto;
import ru.mephi.gpus_agrgtr.utils.DateUtils;

import java.util.*;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryExtractor categoryExtractor;
    private final DateUtils dateUtils;

    private final MessageSender messageSender;

    @Transactional
    public void save(Product product) {
        addLinks(product);
        Optional<Product> foundProduct = find(product.getName());
        if (foundProduct.isPresent()) {
            addStoresFromNewProduct(product, foundProduct.get());
            productRepository.save(foundProduct.get());
        } else {
            productRepository.save(product);
        }
    }

    private void addLinks(Product product) {
        Set<Category> categorySet = getCategories(product);
        categorySet.forEach(category -> category.getProducts().add(product));
        product.setCategories(categorySet)
                .getParameters()
                .forEach(parameter -> parameter.setProduct(product));
        product.getStores()
                .forEach(store -> {
                    store.setProduct(product);
                    store.setDate(dateUtils.getNow());
                });
    }

    public Set<Category> getCategories(Product product) {
        return categoryExtractor.extractCategorySet(product.getName());
    }

    private Optional<Product> find(String productName) {
        return productRepository
                .findProductByName(productName)
                .or(findProductByCategories(categoryExtractor.extractCategorySet(productName)));
    }

    public Supplier<? extends Optional<? extends Product>> findProductByCategories(Set<Category> categories) {
        List<Product> products = productRepository.findAll();
        return () ->
                products.stream()
                        .filter(p -> categoryExtractor.isEqual(getCategories(p), categories))
                        .findFirst();
    }

    private void addStoresFromNewProduct(Product newProduct, Product oldProduct) {
        if (newProduct.getStores().size() != 1) {
            throw new IllegalArgumentException("Wrong number of stores");
        }
        Store newStore = newProduct.getStores().get(0);
        Store lastStore = getLastStoreWithSameUrl(oldProduct, newStore.getUrl());
        if (lastStore == null) {
            addStore(oldProduct, newStore);
            return;
        }
        if (!Objects.equals(lastStore.getCost(), newStore.getCost())) {
            addStore(oldProduct, newStore);
        } else {
            ProductRsDto dto = new ProductRsDto(oldProduct.getId(),lastStore.getCost(),newStore.getCost());
            try {
                messageSender.sendMessage(dto);
            } catch (JsonProcessingException e) {
                System.out.println("Unable to send message");
            }
        }
    }

    private static Store getLastStoreWithSameUrl(Product oldProduct, String url) {
        Store lastStore = null;
        for (Store s : oldProduct.getStores()) {
            if (!s.getUrl().equals(url)) {
                continue;
            }
            if (lastStore == null) {
                lastStore = s;
            }
            if (s.getDate().compareTo(lastStore.getDate()) > 0) {
                lastStore = s;
            }
        }
        return lastStore;
    }

    private static void addStore(Product oldProduct, Store newStore) {
        oldProduct.getStores().add(newStore);
        newStore.setProduct(oldProduct);
    }
}