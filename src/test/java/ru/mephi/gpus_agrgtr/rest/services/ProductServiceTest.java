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
import ru.mephi.gpus_agrgtr.entity.*;
import ru.mephi.gpus_agrgtr.rest.repositories.ProductRepository;
import ru.mephi.gpus_agrgtr.utils.ListUtils;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private static ProductService productService;
    @Mock
    private static ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        CategoryExtractor categoryExtractor = new CategoryExtractor();
        productService = new ProductService(productRepository, categoryExtractor);
    }

    @Test
    void saveNewProduct() {
        Store store = new Store()
                .setName("Super Store")
                .setUrl("www.store.de")
                .setCost(100.0);
        Parameter parameter = new Parameter();
        Product product = new Product()
                .setType(Type.VIDEOCARD)
                .setName("lasdjfldsjfl; a;lsdjfla;skdjfl laskdjf;lsakdfj")
                .setStores(ListUtils.of(store))
                .setParameters(ListUtils.of(parameter))
                .setCountry("aaa")
                .setWeight(100)
                .setWeightWithBox(80);
        List<Product> emptyProductList = new ArrayList<>();

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.when(productRepository.findAll()).thenReturn(emptyProductList);
        Mockito.when(productRepository.findProductByName(Mockito.anyString())).thenReturn(Optional.empty());

        productService.save(product);

        Mockito.verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();
        savedProduct.getStores().forEach(s -> {
            assertEquals(s.getProduct(), product);
            assertNotNull(s.getDate());
        });
        savedProduct.getParameters().forEach(s -> assertEquals(s.getProduct(), product));
        savedProduct.getCategories().forEach(c -> assertTrue(c.getProducts().contains(product)));
    }

    @Test
    void saveExistingProduct() {
        Store newStore = new Store()
                .setName("Super Store")
                .setUrl("www.store.de")
                .setCost(90.0);
        Store oldStore = new Store()
                .setName("Super Store")
                .setUrl("www.store.de")
                .setCost(100.0)
                .setDate(Date.from(Instant.now()));
        Parameter existingParameter = new Parameter();
        Product existingProduct = new Product()
                .setType(Type.VIDEOCARD)
                .setName("existing Product")
                .setStores(ListUtils.of(oldStore))
                .setParameters(ListUtils.of(existingParameter))
                .setCountry("aaa")
                .setWeight(100)
                .setWeightWithBox(80);
        oldStore.setProduct(existingProduct);
        existingParameter.setProduct(existingProduct);
        Parameter newParameter = new Parameter();
        Product newProduct = new Product()
                .setType(Type.VIDEOCARD)
                .setName("new Product")
                .setStores(ListUtils.of(newStore))
                .setParameters(ListUtils.of(newParameter))
                .setCountry("aaa")
                .setWeight(100)
                .setWeightWithBox(80);
        newParameter.setProduct(newProduct);
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.when(productRepository.findProductByName(Mockito.anyString())).thenReturn(Optional.of(existingProduct));

        productService.save(newProduct);

        Mockito.verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();
        assertEquals(savedProduct.getName(), "existing Product");
        savedProduct.getStores().forEach(s -> {
            assertEquals(s.getProduct(), existingProduct);
            assertNotNull(s.getDate());
        });
        assertEquals(savedProduct.getStores().size(), 2);
        savedProduct.getParameters().forEach(s -> assertEquals(s.getProduct(), existingProduct));
    }

    @Test
    void getCategories() {
        Set<Category> categorySet = Set.of(new Category("cat1"), new Category("cat2"));
        Product product = new Product().setName("cat1 cat2").setCategories(categorySet);

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

        Optional<Product> foundProduct = productService.findProductByCategories(productService.getCategories(product1));

        assertTrue(foundProduct.isPresent());
        assertEquals(product2, foundProduct.get());
    }
}