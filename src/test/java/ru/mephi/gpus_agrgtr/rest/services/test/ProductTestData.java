package ru.mephi.gpus_agrgtr.rest.services.test;

import ru.mephi.gpus_agrgtr.entity.*;
import ru.mephi.gpus_agrgtr.utils.ListUtils;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductTestData {
    private List<Product> productsToSave = getProductsToSave();
    private List<Product> existingProducts = getExistingProducts();
    private List<Product> expectedProducts = getExpectedProducts();

    public List<Product> getProductsToSave() {
        if (productsToSave == null) {
            productsToSave = List.of(
                    createProduct(1)
                            .setStores(ListUtils.of(createNewStore(1)))
                            .setParameters(ListUtils.of(
                                            createParameter(1)
                                                    .setCharacteristic(createCharacteristic(1))
                                    )
                            ),
                    createProduct(2)
                            .setStores(ListUtils.of(createNewStore(2)))
                            .setParameters(ListUtils.of(
                                            createParameter(2)
                                                    .setCharacteristic(createCharacteristic(2))
                                    )
                            ),
                    createProduct(3)
                            .setStores(ListUtils.of(createNewStore(3)))
                            .setParameters(ListUtils.of(
                                            createParameter(3)
                                                    .setCharacteristic(createCharacteristic(3))
                                    )
                            ),
                    createProduct(4)
                            .setStores(ListUtils.of(createNewStore(4)))
                            .setParameters(ListUtils.of(
                                            createParameter(4)
                                                    .setCharacteristic(createCharacteristic(4))
                                    )
                            ),
                    createProduct(5)
                            .setName("Product XXX5 YYYY ZZZZZ")
                            .setCategories(getCategories("Product", "XXX", "YYYY", "ZZZZZ"))
                            .setStores(ListUtils.of(createNewStore(5)))
                            .setParameters(ListUtils.of(
                                            createParameter(5)
                                                    .setCharacteristic(createCharacteristic(5))
                                    )
                            )
            );
        }
        return productsToSave;
    }

    public List<Product> getExpectedProducts() {
        if (expectedProducts == null) {
            expectedProducts = List.of(
                    createProduct(1)
                            .setStores(ListUtils.of(createNewStore(1)))
                            .setParameters(ListUtils.of(
                                            createParameter(1)
                                                    .setCharacteristic(createCharacteristic(1))
                                    )
                            ),
                    createProduct(2)
                            .setStores(ListUtils.of(createOldStore(2), createNewStore(2)))
                            .setParameters(ListUtils.of(
                                            createParameter(2)
                                                    .setCharacteristic(createCharacteristic(2))
                                    )
                            ),
                    createProduct(3)
                            .setStores(ListUtils.of(createOldStore(2), createNewStore(3)))
                            .setParameters(ListUtils.of(
                                            createParameter(3)
                                                    .setCharacteristic(createCharacteristic(3))
                                    )
                            ),
                    createProduct(4)
                            .setStores(ListUtils.of(createOldStore(4)))
                            .setParameters(ListUtils.of(
                                            createParameter(4)
                                                    .setCharacteristic(createCharacteristic(4))
                                    )
                            ),
                    createProduct(5)
                            .setStores(ListUtils.of(createOldStore(5), createNewStore(5)))
                            .setParameters(ListUtils.of(
                                            createParameter(5)
                                                    .setCharacteristic(createCharacteristic(5))
                                    )
                            )
            );
            addProductLink(expectedProducts, productsToSave, 0);
            addProductLink(expectedProducts, existingProducts, 1, 2, 3, 4);
            expectedProducts.get(1).getStores().get(0).setCost(10.0);
            expectedProducts.get(4).getStores().get(0).setCost(10.0);
        }
        return expectedProducts;
    }

    public List<Product> getExistingProducts() {
        if (existingProducts == null) {
            existingProducts = List.of(
                    new Product(),
                    createProduct(2).setName("Product XXX2")
                            .setCategories(getCategories("Product", "XXX2"))
                            .setStores(ListUtils.of(createOldStore(2)))
                            .setParameters(ListUtils.of(
                                            createParameter(2)
                                                    .setCharacteristic(createCharacteristic(2))
                                    )
                            ),
                    createProduct(3).setName("Product XXX3")
                            .setCategories(getCategories("Product", "XXX3"))
                            .setStores(ListUtils.of(createOldStore(2)))
                            .setParameters(ListUtils.of(
                                            createParameter(3)
                                                    .setCharacteristic(createCharacteristic(3))
                                    )
                            ),
                    createProduct(4).setName("Product XXX4")
                            .setCategories(getCategories("Product", "XXX4"))
                            .setStores(ListUtils.of(createOldStore(4)))
                            .setParameters(ListUtils.of(
                                            createParameter(4)
                                                    .setCharacteristic(createCharacteristic(4))
                                    )
                            ),
                    createProduct(5)
                            .setStores(ListUtils.of(createOldStore(5)))
                            .setParameters(ListUtils.of(
                                            createParameter(5)
                                                    .setCharacteristic(createCharacteristic(5))
                                    )
                            )
            );
            addProductLink(existingProducts, existingProducts, 1, 2, 3, 4);
            existingProducts.get(1).getStores().get(0).setCost(10.0);
            existingProducts.get(4).getStores().get(0).setCost(10.0);
        }
        return existingProducts;
    }

    private void addProductLink(List<Product> productsTo, List<Product> productsFrom, int... n) {
        for (int i : n) {
            for (Parameter p : productsTo.get(i).getParameters()) {
                p.setProduct(productsFrom.get(i));
            }
            for (Category c : productsTo.get(i).getCategories()) {
                c.getProducts().add(productsFrom.get(i));
            }
            for (Store s : productsTo.get(i).getStores()) {
                s.setProduct(productsFrom.get(i));
            }
        }
    }

    private static Product createProduct(int number) {
        return new Product()
                .setName("Product XXX" + number)
                .setCategories(getCategories("Product", "XXX" + number))
                .setType(Type.VIDEOCARD)
                .setWeight(number)
                .setCountry("Country" + number)
                .setWeightWithBox(number + 10);
    }

    private static Store createNewStore(long number) {
        return new Store()
                .setName("New store" + number)
                .setCost((double) number * 10)
                .setUrl("url." + number)
                .setDate(Date.from(Instant.ofEpochSecond(number + 100)));
    }

    private static Store createOldStore(long number) {
        return new Store()
                .setName("Old store" + number)
                .setCost((double) number * 10)
                .setUrl("url." + number)
                .setDate(Date.from(Instant.ofEpochSecond(number)));
    }

    private static Parameter createParameter(int number) {
        return new Parameter()
                .setName("name" + number)
                .setValue("value" + number)
                ;
    }

    private static Characteristic createCharacteristic(int number) {
        return new Characteristic().setName("characteristic" + number);
    }

    private static Set<Category> getCategories(String... strings) {
        Set<Category> set = new HashSet<>();
        for (String s :
                strings) {
            set.add(new Category(s));
        }
        return set;
    }
}