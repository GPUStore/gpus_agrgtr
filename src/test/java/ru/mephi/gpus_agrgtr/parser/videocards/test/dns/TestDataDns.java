package ru.mephi.gpus_agrgtr.parser.videocards.test.dns;

import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.entity.Store;
import ru.mephi.gpus_agrgtr.entity.Type;
import ru.mephi.gpus_agrgtr.parser.videocards.test.TestData;

import java.util.List;

public class TestDataDns extends TestData {

    public static List<Product> getTestProducts() {
        return List.of(
                createProduct(1)
                        .setStores(List.of(createStore(1)))
                        .setParameters(List.of(
                                createParameter(1)
                                        .setCharacteristic(createCharacteristic(1)),
                                createParameter(2)
                                        .setCharacteristic(createCharacteristic(1)),
                                createParameter(3)
                                        .setCharacteristic(createCharacteristic(2)),
                                createParameter(4)
                                        .setCharacteristic(createCharacteristic(2)))),
                createProduct(2)
                        .setStores(List.of(createStore(2)))
                        .setParameters(List.of(
                                createParameter(1)
                                        .setCharacteristic(createCharacteristic(1)),
                                createParameter(2)
                                        .setCharacteristic(createCharacteristic(1)),
                                createParameter(3)
                                        .setCharacteristic(createCharacteristic(2)),
                                createParameter(4)
                                        .setCharacteristic(createCharacteristic(2))))
        );
    }

    protected static Store createStore(int number) {
        return new Store()
                .setName("store")
                .setCost(1.0)
                .setUrl("url" + number)
                ;
    }

    protected static Product createProduct(int number) {
        return new Product()
                .setType(Type.VIDEOCARD)
                .setName("PRODUCT" + number)
                .setCountry(null)
                .setWeight(0.0)
                .setWeightWithBox(0.0)
                ;
    }
}
