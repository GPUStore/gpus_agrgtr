package ru.mephi.gpus_agrgtr.parser.videocards.technopark;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.mephi.gpus_agrgtr.entity.*;
import ru.mephi.gpus_agrgtr.parser.Parser;
import ru.mephi.gpus_agrgtr.parser.videocards.technopark.response.FullDTO;
import ru.mephi.gpus_agrgtr.parser.videocards.technopark.response.Response;
import ru.mephi.gpus_agrgtr.parser.videocards.technopark.response.SpecificationsDTO;
import ru.mephi.gpus_agrgtr.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.mephi.gpus_agrgtr.utils.StringUtils.getByPattern;

@Slf4j
@Component
public class TechnoparkParser extends Parser {
    private final String storeUrl;
    private final String requestUrl;
    private static final String PATH_FOR_COUNT_PAGES = "button.tp-pagination-button";
    private static final String PATH_FOR_COSTS = "div.product-prices__price";
    private static final String PATH_FOR_LINKS = "a.product-card-link.product-card-big__title";
    private static final String PATH_FOR_NAMES = "a.product-card-link.product-card-big__title";
    private static final String PATH_FOR_ARTICLES = "div.product-card-big__code";
    private static final String REQUEST_BODY = "{" +
            "\"operationName\": \"ProductSpecifications\"," +
            "\"variables\": {" +
            "     \"article\": \"%s\"," +
            "     \"token\": \"985bb5a6c945fdbd3ba16002d07b0c29\"," +
            "     \"cityId\": \"36966\"" +
            "}," +
            "\"query\": \"query ProductSpecifications($token: String!, $cityId: ID!, $article: ID) " +
            "           @access(token: $token) @city(id: $cityId) { productV2(article: $article) " +
            "           { id specifications { barcode country full { name list { name value __typename }" +
            "           __typename } grossWeight netWeight height instructionUrl length schemeUrl warranty" +
            "           width __typename } __typename }}\"" +
            "}";
    private static final String NUMBER = "\\d+";
    private static final String NAME = "[^а-яА-Я]";

    public TechnoparkParser(@Value("${technopark.url.list}") String url,
                            @Value("${technopark.url.product}") String requestUrl,
                            @Value("${technopark.url.base}") String storeUrl,
                            @Value("${technopark.name}") String storeName,
                            ObjectMapper mapper) {
        super(url, storeName, mapper);
        this.requestUrl = requestUrl;
        this.storeUrl = storeUrl;
    }

    @Override
    public List<Product> getAllProducts() {
        int numberOfPage = 1;
        List<Product> products = new ArrayList<>();
        Document page = getPage(numberOfPage);
        int countPages = getCountPages(page);
        while (true) {
            List<Double> costs = getCosts(page);
            List<String> links = getLinks(page);
            List<String> names = getNames(page);
            List<String> articles = getArticles(page);
            ifNotEqualsSizeThrowException(costs, links, names, articles);
            for (int i = 0; i < articles.size(); i++) {
                try {
                    products.add(getProduct(costs.get(i), links.get(i), names.get(i), articles.get(i)));
                } catch (RuntimeException e) {
                    log.info("Product with article [" + articles.get(i) + "] was not created.");
                }
            }
            if (++numberOfPage <= countPages) {
                page = getPage(numberOfPage);
            } else {
                break;
            }
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

    private List<Double> getCosts(Document page) {
        return page.select(PATH_FOR_COSTS).stream()
                .map(Element::ownText)
                .map(textWithCost -> getByPattern(NUMBER, textWithCost))
                .map(Double::parseDouble)
                .toList();
    }

    private List<String> getNames(Document page) {
        return page.select(PATH_FOR_NAMES).stream()
                .map(element -> element.attr("title"))
                .map(title -> getByPattern(NAME, title))
                .map(name -> name.trim().toUpperCase())
                .toList();
    }

    private List<String> getArticles(Document page) {
        return page.select(PATH_FOR_ARTICLES).stream()
                .map(Element::ownText)
                .map(textWithArticle -> getByPattern(NUMBER, textWithArticle))
                .toList();
    }

    private List<String> getLinks(Document page) {
        return page.select(PATH_FOR_LINKS).stream()
                .map(element -> element.attr("href"))
                .map(href -> storeUrl + href)
                .toList();
    }

    private void ifNotEqualsSizeThrowException(List<Double> costs, List<String> links, List<String> names, List<String> articles) {
        int size = costs.size();
        if (size != links.size() || size != articles.size() || size != names.size()) {
            throw new RuntimeException("Website data integrity error.");
        }
    }

    private Product getProduct(Double cost, String link, String name, String article) {
        SpecificationsDTO specificationsDTO = requestData(article);
        Store store = new Store()
                .setName(storeName)
                .setUrl(link)
                .setCost(cost);
        return new Product()
                .setType(Type.VIDEOCARD)
                .setName(name)
                .setStores(ListUtils.of(store))
                .setCountry(specificationsDTO.getCountry())
                .setWeight(specificationsDTO.getNetWeight())
                .setWeightWithBox(specificationsDTO.getGrossWeight())
                .setParameters(toParameters(specificationsDTO.getFull()));
    }

    private SpecificationsDTO requestData(String article) {
        String requestBody = buildRequest(article);
        Response response = post(requestUrl, requestBody, Response.class);
        return response.getData().getProductV2().getSpecifications();
    }

    private String buildRequest(String article) {
        return String.format(REQUEST_BODY, article);
    }

    private List<Parameter> toParameters(List<FullDTO> fullsDTO) {
        return fullsDTO.stream()
                .flatMap(fullDTO -> {
                    Characteristic characteristic = toCharacteristic(fullDTO.getName());
                    return fullDTO.getList().stream()
                            .map(paramDTO -> new Parameter()
                                    .setName(paramDTO.getName())
                                    .setValue(paramDTO.getValue())
                                    .setCharacteristic(characteristic)
                            );
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Characteristic toCharacteristic(String name) {
        return switch (name) {
            case "Основные характеристики" -> new Characteristic().setName("Основные характеристики");
            case "Видое разъемы", "Математический блок", "Поддержка стандартов", "Технические характеристики" ->
                    new Characteristic().setName("Технические характеристики");
            case "Разъемы" -> new Characteristic().setName("Разъемы");
            case "Комплектация" -> new Characteristic().setName("Комплектация");
            case "Размер и вес" -> new Characteristic().setName("Размер и вес");
            default -> new Characteristic().setName("Дополнительные характеристики");
        };
    }
}
