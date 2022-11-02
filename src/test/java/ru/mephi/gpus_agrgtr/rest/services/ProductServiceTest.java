package ru.mephi.gpus_agrgtr.rest.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mephi.gpus_agrgtr.category.CategoryExtractor;
import ru.mephi.gpus_agrgtr.entity.Category;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.rest.repositories.CategoryRepository;
import ru.mephi.gpus_agrgtr.rest.repositories.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static ProductService productService;
    private static CategoryService categoryService;
    private static CategoryExtractor categoryExtractor;
    @Mock
    private static CategoryRepository categoryRepository;
    @Mock
    private static ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryExtractor = new CategoryExtractor();
        categoryService = new CategoryService(categoryRepository);
        productService = new ProductService(productRepository, categoryService, categoryExtractor);
    }

    @Test
    void getCategoriesOfExistingProduct() {
        Set<Category> categorySet = Set.of(new Category("cat1"), new Category("cat2"));
        Product product = new Product().setName("cat1 cat2").setCategories(categorySet);

        Mockito.when(productRepository.findProductByName("cat1 cat2")).thenReturn(Optional.of(product));

        Set<Category> categoriesFromService = productService.getCategories(product);

        assertTrue(categoriesFromService.containsAll(categorySet) && categorySet.size() == categoriesFromService.size());
    }

    @Test
    void getCategoriesOfNotExistingProduct() {
        Set<Category> categorySet = Set.of(new Category("cat1"), new Category("cat2"));
        Product product = new Product().setName("cat1 cat2").setCategories(categorySet);

        Mockito.when(productRepository.findProductByName("cat1 cat2")).thenReturn(Optional.empty());

        Set<Category> categoriesFromService = productService.getCategories(product);

        assertTrue(categoriesFromService.containsAll(categorySet) && categorySet.size() == categoriesFromService.size());
    }

    @Test
    void findProductByCategories() {
        Set<Category> categorySet1 = Set.of(new Category("cat1"), new Category("cat2"));
        Product product1 = new Product().setName("cat1 cat2").setCategories(categorySet1);
        Set<Category> categorySet2 = Set.of(
                new Category("cat1"),
                new Category("cat2"),
                new Category("cat3"),
                new Category("cat4"));
        Product product2 = new Product().setName("cat1 cat2 cat3 cat4").setCategories(categorySet2);

        Mockito.when(productRepository.findAll()).thenReturn(List.of(product2));

        Optional<Product> foundProduct = productService.findProductByCategories(product1);

        assertTrue(foundProduct.isPresent());
        assertEquals(product2, foundProduct.get());
    }
}