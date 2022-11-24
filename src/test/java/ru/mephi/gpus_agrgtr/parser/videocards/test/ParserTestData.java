package ru.mephi.gpus_agrgtr.parser.videocards.test;

import ru.mephi.gpus_agrgtr.entity.*;
import ru.mephi.gpus_agrgtr.utils.ListUtils;

import java.util.List;

public class ParserTestData {

    public static List<Product> getTestProducts() {
        return List.of(
                createProduct(1)
                        .setStores(ListUtils.of(createStore(1)))
                        .setParameters(ListUtils.of(
                                createParameter(1)
                                        .setCharacteristic(createCharacteristic(1)),
                                createParameter(2)
                                        .setCharacteristic(createCharacteristic(1)),
                                createParameter(3)
                                        .setCharacteristic(createCharacteristic(2)),
                                createParameter(4)
                                        .setCharacteristic(createCharacteristic(2)))),
                createProduct(2)
                        .setStores(ListUtils.of(createStore(2)))
                        .setParameters(ListUtils.of(
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

    protected static Product createProduct(int number) {
        return new Product()
                .setType(Type.VIDEOCARD)
                .setName("PRODUCT" + number)
                .setCountry("country" + number)
                .setWeight(number)
                .setWeightWithBox(number)
                ;
    }

    protected static Store createStore(int number) {
        return new Store()
                .setName("store" + number)
                .setCost(1.0)
                .setUrl("url" + number)
                ;
    }

    protected static Parameter createParameter(int number) {
        return new Parameter()
                .setName("name" + number)
                .setValue("value" + number)
                ;
    }

    protected static Characteristic createCharacteristic(int number) {
        return new Characteristic().setName("characteristic" + number);
    }
}
