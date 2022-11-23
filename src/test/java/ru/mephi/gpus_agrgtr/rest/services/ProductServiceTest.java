package ru.mephi.gpus_agrgtr.rest.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mephi.gpus_agrgtr.category.CategoryExtractor;
import ru.mephi.gpus_agrgtr.entity.Category;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.rest.repositories.ProductRepository;
import ru.mephi.gpus_agrgtr.rest.services.test.AbstractProductTest;
import ru.mephi.gpus_agrgtr.rest.services.test.ProductTestData;
import ru.mephi.gpus_agrgtr.utils.DateUtils;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest extends AbstractProductTest {
    private static ProductService productService;
    @Mock
    private static ProductRepository productRepository;
    @Mock
    private static DateUtils dateUtils;
    @Mock
    private static CategoryExtractor categoryExtractor;

    private static final ProductTestData td = new ProductTestData();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepository, categoryExtractor, dateUtils);
    }


    @Test
    void saveNewProduct() {
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.when(productRepository.findAll()).thenReturn(new ArrayList<>());
        Mockito.when(productRepository.findProductByName(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(dateUtils.getNow()).thenReturn(Date.from(Instant.ofEpochSecond(101L)));
        Mockito.when(categoryExtractor.extractCategorySet(Mockito.anyString())).thenReturn(Set.of(new Category("Product"), new Category("XXX1")));
        productService.save(td.getProductsToSave().get(0));

        Mockito.verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();
        assertProducts(td.getExpectedProducts().get(0), savedProduct);
    }

    @Test
    void saveExistingProduct() {
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.when(productRepository.findAll()).thenReturn(new ArrayList<>());
        Mockito.when(productRepository.findProductByName(Mockito.anyString())).thenReturn(Optional.ofNullable(td.getExistingProducts().get(1)));
        Mockito.when(dateUtils.getNow()).thenReturn(Date.from(Instant.ofEpochSecond(102L)));

        productService.save(td.getProductsToSave().get(1));

        Mockito.verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();
        assertProducts(td.getExpectedProducts().get(1), savedProduct);
    }


    @Test
    void saveExistingProductWithNewStore() {
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.when(productRepository.findAll()).thenReturn(new ArrayList<>());
        Mockito.when(productRepository.findProductByName(Mockito.anyString())).thenReturn(Optional.ofNullable(td.getExistingProducts().get(2)));
        Mockito.when(dateUtils.getNow()).thenReturn(Date.from(Instant.ofEpochSecond(103L)));

        productService.save(td.getProductsToSave().get(2));

        Mockito.verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();
        assertProducts(td.getExpectedProducts().get(2), savedProduct);
    }


    @Test
    void saveExistingProductSamePrice() {
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.when(productRepository.findAll()).thenReturn(new ArrayList<>());
        Mockito.when(productRepository.findProductByName(Mockito.anyString())).thenReturn(Optional.ofNullable(td.getExistingProducts().get(3)));
        Mockito.when(dateUtils.getNow()).thenReturn(Date.from(Instant.ofEpochSecond(104L)));

        productService.save(td.getProductsToSave().get(3));

        Mockito.verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();
        assertProducts(td.getExpectedProducts().get(3), savedProduct);
    }

    @Test
    void saveProductByCategories() {
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.when(productRepository.findAll()).thenReturn(List.of(td.getExistingProducts().get(4)));
        Mockito.when(productRepository.findProductByName(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(dateUtils.getNow()).thenReturn(Date.from(Instant.ofEpochSecond(105L)));
        Mockito.when(productRepository.findProductByName(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(categoryExtractor.isEqual(Mockito.anySet(), Mockito.anySet())).thenReturn(true);

        productService.save(td.getProductsToSave().get(4));

        Mockito.verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();
        assertProducts(td.getExpectedProducts().get(4), savedProduct);
    }

    @Test
    void getCategories() {
        Set<Category> categorySet = Set.of(new Category("cat1"), new Category("cat2"));
        Product product = new Product().setName("cat1 cat2").setCategories(categorySet);

        Mockito.when(categoryExtractor.extractCategorySet(Mockito.anyString())).thenReturn(Set.of(new Category("cat1"), new Category("cat2")));

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
        Mockito.when(categoryExtractor.isEqual(Mockito.anySet(), Mockito.anySet())).thenReturn(true);

        Product foundProduct = productService.findProductByCategories(productService.getCategories(product1)).get().get();

        assertNotNull(foundProduct);
        assertEquals(product2, foundProduct);
    }
}