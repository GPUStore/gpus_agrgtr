package ru.mephi.gpus_agrgtr;

import ru.mephi.gpus_agrgtr.entity.Characteristic;
import ru.mephi.gpus_agrgtr.entity.Parameter;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.entity.Store;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractTest {

    protected void assertProducts(Product expected, Product actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getWeight(), actual.getWeight());
        assertEquals(expected.getWeightWithBox(), actual.getWeightWithBox());
        assertEquals(expected.getStores().size(), actual.getStores().size());
        for (int i = 0; i < expected.getStores().size(); i++) {
            List<Store> expectedStores = expected.getStores();
            List<Store> actualStores = actual.getStores();
            assertStores(expectedStores.get(i), actualStores.get(i));
        }
        assertEquals(expected.getParameters().size(), actual.getParameters().size());
        for (int i = 0; i < expected.getParameters().size(); i++) {
            List<Parameter> expectedParameters = expected.getParameters();
            List<Parameter> actualParameters = actual.getParameters();
            assertParameters(expectedParameters.get(i), actualParameters.get(i));
        }
    }

    protected void assertStores(Store expected, Store actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCost(), actual.getCost());
        assertEquals(expected.getUrl(), actual.getUrl());
    }

    protected void assertParameters(Parameter expected, Parameter actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
        assertCharacteristics(expected.getCharacteristic(), actual.getCharacteristic());
    }

    protected void assertCharacteristics(Characteristic expected, Characteristic actual) {
        assertEquals(expected.getName(), actual.getName());
    }
}