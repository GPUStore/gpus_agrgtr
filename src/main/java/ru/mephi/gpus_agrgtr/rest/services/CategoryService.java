package ru.mephi.gpus_agrgtr.rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mephi.gpus_agrgtr.category.CategoryExtractor;
import ru.mephi.gpus_agrgtr.entity.Category;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.rest.repositories.CategoryRepository;
import ru.mephi.gpus_agrgtr.rest.repositories.ProductRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryExtractor idMaker;

    public void saveInRepository(Set<Category> cats) {
        cats.forEach(category -> {
            if (!categoryRepository.existsByName(category.getName())){
                categoryRepository.save(category);
            }
        });
    }


    public Set<Category> getCategories(Product product) {
        return idMaker.extractCategorySet(product.getName());
    }

    public Product findProductByCategories(Product product) {
        Set<Category> categories = getCategories(product);
        List<Product> products = productRepository.findAll();
        return products.stream().filter(p -> idMaker.AreEqual(getCategories(p),categories)).findFirst().orElse(null);
    }
}
