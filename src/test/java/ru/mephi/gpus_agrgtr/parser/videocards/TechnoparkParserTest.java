package ru.mephi.gpus_agrgtr.parser.videocards;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.mephi.gpus_agrgtr.entity.Characteristic;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.parser.videocards.entity.Response;
import ru.mephi.gpus_agrgtr.parser.videocards.technopark.TechnoparkParser;
import ru.mephi.gpus_agrgtr.parser.videocards.test.AbstractParserTest;
import ru.mephi.gpus_agrgtr.parser.videocards.test.TestData;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TechnoparkParserTest extends AbstractParserTest {

    private static final String URL_STORE = "";
    private static final String URL_FOR_HTML_PAGE = "https://TechnoparkHTML%d";
    private static final String URL_FOR_DATA = "https://TechnoparkJSON";
    private static final String REQUEST = "{" +
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

    private TechnoparkParser technoparkParser;

    @BeforeEach
    public void init() {
        technoparkParser = Mockito.spy(
                new TechnoparkParser(
                        URL_FOR_HTML_PAGE,
                        URL_FOR_DATA,
                        URL_STORE,
                        "store1",
                        null
                )
        );
    }

    private ObjectMapper getObjectMapper() {
        return Jackson2ObjectMapperBuilder
                .json()
                .build()
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    @Test
    void getAllProducts() throws IOException {
        Mockito.doReturn(getTestDocument("src/test/repository/technopark/technopark.html"))
                .when(technoparkParser).get(String.format(URL_FOR_HTML_PAGE, 1));
        Mockito.doReturn(getObjectMapper().readValue(getTestJsonString("src/test/repository/technopark/technopark1.json"), Response.class))
                .when(technoparkParser).post(URL_FOR_DATA, String.format(REQUEST, 1), Response.class);
        Mockito.doReturn(getObjectMapper().readValue(getTestJsonString("src/test/repository/technopark/technopark1.json"), Response.class))
                .when(technoparkParser).post(URL_FOR_DATA, String.format(REQUEST, 2), Response.class);
        Mockito.doReturn(new Characteristic().setName("characteristic1")).when(technoparkParser).toCharacteristic("characteristic1");
        Mockito.doReturn(new Characteristic().setName("characteristic2")).when(technoparkParser).toCharacteristic("characteristic2");

        List<Product> actualProducts = technoparkParser.getAllProducts();
        List<Product> expectedProducts = TestData.getTestProducts();

        assertEquals(expectedProducts.size(), actualProducts.size());
        assertProducts(expectedProducts.get(0), actualProducts.get(0));
    }
}