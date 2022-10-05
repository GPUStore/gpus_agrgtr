//package ru.mephi.gpus_agrgtr.parser.videocards.wildberies;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import ru.mephi.gpus_agrgtr.entity.Characteristic;
//import ru.mephi.gpus_agrgtr.entity.Product;
//import ru.mephi.gpus_agrgtr.parser.Parser;
//import ru.mephi.gpus_agrgtr.parser.videocards.entity.wild.Response;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class WildberriesParser extends Parser {
//    private  final  String baseWorkUrl;
//
//    WildberriesParser(@Value("${url.wildberries.example}") String url,
//                      @Value("${url.wildberries.exampleProduct}")  String baseWorkUrl, ObjectMapper mapper) {
//       // super(url, mapper);
//        this.baseWorkUrl = baseWorkUrl;
//    }
//
//
//    @Override
//    public Characteristic toCharacteristic(String name) {
//        return null;
//    }
//
//    @Override
//    protected List<Product> getAllProducts() throws Exception {
//        List<Product> products = new ArrayList<>();
//        try {
//            Document page = getPage(1);
//            try {
//                requestProduct(baseWorkUrl);
////                Product product = requestProduct(url)
////                        .setStore(List.of(new Store()
////                                .setName("Technopark")
////                                .setUrl(links.get(j))
////                                .setCost(costs.get(j))));
////                product.setName((product.getName() + " " + serialNumbers.get(j)).trim().toUpperCase());
////                products.add(product);
//            } catch (RuntimeException e) {
//                //log. Продукт с артиклем={} не создан
//            }
//        } catch (Exception e) {
//            //log. Парсинг страницы не удался
//        }
//        return products;
//    }
//
//    private Document getPage(int numberOfPage) {
//        return get(String.format(url, numberOfPage));
//    }
//
//
//    private <T> T getCardJson(String requestLink,Class<T> classT) {
//        try {
//            String json = Jsoup.connect(requestLink)
//                    .ignoreContentType(true)
//                    .method(Connection.Method.GET)
//                    .execute()
//                    .body();
//            return mapper.readValue(json, classT);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private Product requestProduct(String article) {
//        Response response = getCardJson(article, Response.class);
//        return null;
//    }
////    private Product createProduct(Response response) {
////        SpecificationsDTO specificationsDTO = response.getData().getProductV2().getSpecifications();
////        return new Product()
////                .setType(Type.VIDEOCARD)
////                .setCountry(specificationsDTO.getCountry())
////                .setWeight(specificationsDTO.getNetWeight())
////                .setWeightWithBox(specificationsDTO.getGrossWeight());
////    }
//}
