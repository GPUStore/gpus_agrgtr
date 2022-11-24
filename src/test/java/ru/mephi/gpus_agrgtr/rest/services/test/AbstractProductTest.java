package ru.mephi.gpus_agrgtr.rest.services.test;

import ru.mephi.gpus_agrgtr.AbstractTest;
import ru.mephi.gpus_agrgtr.entity.Parameter;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.entity.Store;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractProductTest extends AbstractTest {

    @Override
    protected void assertProducts(Product expected, Product actual) {
        super.assertProducts(expected, actual);
        assertEquals(expected.getCategories(), actual.getCategories());
    }

    @Override
    protected void assertStores(Store expected, Store actual) {
        super.assertStores(expected, actual);
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getProduct(), actual.getProduct());
    }

    @Override
    protected void assertParameters(Parameter expected, Parameter actual) {
        super.assertParameters(expected, actual);
        assertEquals(expected.getProduct(), actual.getProduct());
    }
}
