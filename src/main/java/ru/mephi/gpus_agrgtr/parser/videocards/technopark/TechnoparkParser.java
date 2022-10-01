package ru.mephi.gpus_agrgtr.parser.videocards.technopark;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.mephi.gpus_agrgtr.entity.*;
import ru.mephi.gpus_agrgtr.parser.Parser;
import ru.mephi.gpus_agrgtr.parser.videocards.entity.FullDTO;
import ru.mephi.gpus_agrgtr.parser.videocards.entity.ParamDTO;
import ru.mephi.gpus_agrgtr.parser.videocards.entity.Response;
import ru.mephi.gpus_agrgtr.parser.videocards.entity.SpecificationsDTO;

import java.util.ArrayList;
import java.util.List;

import static ru.mephi.gpus_agrgtr.utils.StringUtils.getByPattern;

@Component
public class TechnoparkParser extends Parser {
    private final String baseUrl;
    private final String requestUrl;
    private static final String PATH_FOR_SERIAL_NUMBER = "div.product-card-big__name";
    private static final String PATH_FOR_ARTICLES = "div.product-card-big__code";
    private static final String PATH_FOR_COSTS = "div.product-prices__price";
    private static final String PATH_FOR_LINKS = "a.product-card-link.product-card-big__title";
    private static final String PATH_FOR_COUNT_PAGES = "button.tp-pagination-button";
    private static final String REQUEST_BODY = "{" +
            "\"operationName\": \"ProductSpecifications\"," +
            "\"variables\": {" +
            "     \"article\": \"%s\"," +
            "     \"token\": \"%s\"," +
            "     \"cityId\": \"%s\"" +
            "}," +
            "\"query\": \"query ProductSpecifications($token: String!, $cityId: ID!, $article: ID) " +
            "           @access(token: $token) @city(id: $cityId) { productV2(article: $article) " +
            "           { id specifications { barcode country full { name list { name value __typename }" +
            "           __typename } grossWeight netWeight height instructionUrl length schemeUrl warranty" +
            "           width __typename } __typename }}\"" +
            "}";
    private static final String SERIAL_NUMBER = "\\(.+\\)";
    private static final String NUMBER = "\\d+";
    private static final String MOSCOW = "36966";
    private static final String TOKEN = "985bb5a6c945fdbd3ba16002d07b0c29";

    public TechnoparkParser(@Value("${url.technopark.list}") String url,
                            @Value("${url.technopark.product}") String requestUrl,
                            @Value("${url.technopark.base}") String baseUrl,
                            ObjectMapper mapper) {
        super(url, mapper);
        this.requestUrl = requestUrl;
        this.baseUrl = baseUrl;
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

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try {
            Document page = getPage(1);
            int countPages = getCountPages(page);
            for (int numberOfPage = 1; numberOfPage <= countPages; numberOfPage++) {
                List<Double> costs = getCosts(page);
                List<String> links = getLinks(page);
                List<String> serialNumbers = getSerialNumbers(page);
                List<String> articles = getArticles(page);
                for (int j = 0; j < articles.size(); j++) {
                    try {
                        Product product = requestProduct(articles.get(j))
                                .setStore(List.of(new Store()
                                        .setName("Technopark")
                                        .setUrl(links.get(j))
                                        .setCost(costs.get(j))));
                        product.setName((product.getName() + " " + serialNumbers.get(j)).trim().toUpperCase());
                        products.add(product);
                    } catch (RuntimeException e) {
                        //log. Продукт с артиклем={} не создан
                    }
                }
                page = getPage(numberOfPage);
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

    private List<Double> getCosts(Document page) {
        return page.select(PATH_FOR_COSTS).stream()
                .map(Element::ownText)
                .map(textWithCost -> getByPattern(NUMBER, textWithCost))
                .map(Double::parseDouble)
                .toList();
    }

    private List<String> getSerialNumbers(Document page) {
        return page.select(PATH_FOR_SERIAL_NUMBER)
                .stream().map(Element::ownText)
                .map(textWithId -> getByPattern(SERIAL_NUMBER, textWithId))
                .toList();
    }

    private List<String> getLinks(Document page) {
        return page.select(PATH_FOR_LINKS).stream()
                .map(element -> element.attr("href"))
                .map(link -> baseUrl + link)
                .toList();
    }

    private List<String> getArticles(Document page) {
        return page.select(PATH_FOR_ARTICLES).stream()
                .map(Element::ownText)
                .map(textWithArticle -> getByPattern(NUMBER, textWithArticle))
                .toList();
    }

    private Product requestProduct(String article) {
        String requestBody = buildRequest(article);
        Response response = post(requestUrl, requestBody, Response.class);
        return createProduct(response);
    }

    private String buildRequest(String article) {
        return String.format(REQUEST_BODY, article, TOKEN, MOSCOW);
    }

    private Product createProduct(Response response) {
        SpecificationsDTO specificationsDTO = response.getData().getProductV2().getSpecifications();
        return new Product()
                .setType(Type.VIDEOCARD)
                .setCountry(specificationsDTO.getCountry())
                .setWeight(specificationsDTO.getNetWeight())
                .setWeightWithBox(specificationsDTO.getGrossWeight())
                .setParameters(parseFull(specificationsDTO.getFull()))
                .setName(buildName(specificationsDTO.getFull()));
    }

    private List<Parameter> parseFull(List<FullDTO> fullsDTO) {
        return fullsDTO.stream()
                .flatMap(fullDTO -> {
                    Characteristic characteristic = toCharacteristic(fullDTO.getName());
                    return toParams(fullDTO.getList(), characteristic).stream();
                })
                .toList();

    }

    private List<Parameter> toParams(List<ParamDTO> list, Characteristic characteristic) {
        return list.stream()
                .map(paramDTO -> new Parameter()
                        .setName(paramDTO.getName())
                        .setValue(paramDTO.getValue())
                        .setCharacteristic(characteristic)
                )
                .toList();
    }

    private String buildName(List<FullDTO> fullsDTO) {
        List<ParamDTO> mainParams = getValueByFullName(fullsDTO, "Основные характеристики");
        List<ParamDTO> technicalParams = getValueByFullName(fullsDTO, "Технические характеристики");
        return getValueByParamName(mainParams, "Разработчик видеокарты") + " " +
                getValueByParamName(mainParams, "Видеопроцессор") + " " +
                getValueByParamName(technicalParams, "Объем видеопамяти");
    }

    private List<ParamDTO> getValueByFullName(List<FullDTO> fullsDTO, String fullName) {
        for (FullDTO fullDTO : fullsDTO) {
            if (fullDTO.getName().equals(fullName)) {
                return fullDTO.getList();
            }
        }
        throw new RuntimeException();
    }

    private String getValueByParamName(List<ParamDTO> paramsDTO, String paramName) {
        for (ParamDTO paramDTO : paramsDTO) {
            if (paramDTO.getName().equals(paramName)) {
                return paramDTO.getValue();
            }
        }
        throw new RuntimeException();
    }
}
