package ru.mephi.gpus_agrgtr.parser.videocards.dns;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import ru.mephi.gpus_agrgtr.entity.Characteristic;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.parser.Parser;
import ru.mephi.gpus_agrgtr.parser.videocards.entity.wild.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DnsParser extends Parser {
    private final String storeUrl;

    private static final String PATH_FOR_COUNT_PAGES = "button.pagination-widget__page";
    private static final String PATH_FOR_ID = "https://www.dns-shop.ru/ajax-state/buy-button/?cityId=10&langId=ru&v=2";
    private static final String PATH_FOR_COSTS = "div.product-prices__price";
    private static final String PATH_FOR_LINKS = "a.product-card-link.product-card-big__title";
    private static final String PATH_FOR_NAMES = "a.product-card-link.product-card-big__title";
    private static final String PATH_FOR_ARTICLES = "div.product-card-big__code";


    public DnsParser(@Value("${dns.url.base}") String url,
                            @Value("${dns.url.list}") String storeUrl,
                            @Value("${dns.url.name}") String storeName,
                            ObjectMapper mapper) {
        super(url,storeName, mapper);
        this.storeUrl = storeUrl;
    }

    @Override
    public Characteristic toCharacteristic(String name) {
        return null;
    }

    @Override
    protected List<Product> getAllProducts() throws Exception {
        List<Product> products = new ArrayList<>();
        try {
            Document page = getPage(1);
            int countPages = getCountPages(page);
            try {
                requestProduct(storeUrl);

//                Product product = requestProduct(url)
//                        .setStore(List.of(new Store()
//                                .setName("Technopark")
//                                .setUrl(links.get(j))
//                                .setCost(costs.get(j))));
//                product.setName((product.getName() + " " + serialNumbers.get(j)).trim().toUpperCase());
//                products.add(product);
            } catch (RuntimeException e) {
                //log. Продукт с артиклем={} не создан
            }
        } catch (Exception e) {
            //log. Парсинг страницы не удался
        }
        return products;
    }

    private Document getPage(int numberOfPage) {
        return get(String.format(url, numberOfPage));
    }

    private int getCountPages(Document page) {
        String count = page.select(PATH_FOR_COUNT_PAGES).last().text();
        return Integer.parseInt(count);
    }

    private <T> T getCardJson(String requestLink,Class<T> classT) {
        try {
            String json = Jsoup.connect(requestLink)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute()
                    .body();
            return mapper.readValue(json, classT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Product requestProduct(String article) {
        Response response = getCardJson(article, Response.class);
        return null;
    }
}
