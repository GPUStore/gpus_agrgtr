package ru.mephi.gpus_agrgtr.parser.videocards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mephi.gpus_agrgtr.entity.Characteristic;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.parser.videocards.technopark.TechnoparkParser;
import ru.mephi.gpus_agrgtr.parser.videocards.technopark.response.Response;
import ru.mephi.gpus_agrgtr.parser.videocards.test.AbstractParserTest;
import ru.mephi.gpus_agrgtr.parser.videocards.test.TestData;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    private static final String TEST_URL_FOR_INCORRECT_HTML = "src/test/repository/technopark/technoparkIncorrect.html";
    private static final String TEST_URL_FOR_CORRECT_HTML = "src/test/repository/technopark/technoparkCorrect.html";
    private static final String TEST_URL_FOR_DATA_1 = "src/test/repository/technopark/technopark1.json";
    private static final String TEST_URL_FOR_DATA_2 = "src/test/repository/technopark/technopark2.json";

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

    @Test
    void getAllProductsTest() throws IOException {
        Mockito.doReturn(getTestDocument(TEST_URL_FOR_CORRECT_HTML))
                .when(technoparkParser).get(String.format(URL_FOR_HTML_PAGE, 1));
        Mockito.doReturn(getObjectMapper().readValue(getTestJsonString(TEST_URL_FOR_DATA_1), Response.class))
                .when(technoparkParser).post(URL_FOR_DATA, String.format(REQUEST, 1), Response.class);
        Mockito.doReturn(getObjectMapper().readValue(getTestJsonString(TEST_URL_FOR_DATA_2), Response.class))
                .when(technoparkParser).post(URL_FOR_DATA, String.format(REQUEST, 2), Response.class);
        Mockito.doReturn(new Characteristic().setName("characteristic1"))
                .when(technoparkParser).toCharacteristic("characteristic1");
        Mockito.doReturn(new Characteristic().setName("characteristic2"))
                .when(technoparkParser).toCharacteristic("characteristic2");

        List<Product> actualProducts = technoparkParser.getAllProducts();
        List<Product> expectedProducts = TestData.getTestProducts();

        assertEquals(expectedProducts.size(), actualProducts.size());
        assertProducts(expectedProducts.get(0), actualProducts.get(0));
    }

    @Test
    void getAllProductsTestWithIncorrectUrlHTML() {
        assertThrows(
                RuntimeException.class,
                () -> technoparkParser.getAllProducts(),
                "Can not parse this page: " + URL_FOR_HTML_PAGE
        );
    }

    @Test
    void getAllProductsTestWithNotEqualsSizeLists() throws IOException {
        Mockito.doReturn(getTestDocument(TEST_URL_FOR_INCORRECT_HTML))
                .when(technoparkParser).get(String.format(URL_FOR_HTML_PAGE, 1));
        assertThrows(
                RuntimeException.class,
                () -> technoparkParser.getAllProducts(),
                "Website data integrity error."
        );
    }

    @Test
    void toCharacteristicTest() {
        assertEquals(new Characteristic().setName("Основные характеристики"),
                technoparkParser.toCharacteristic("Основные характеристики"));
        assertEquals(new Characteristic().setName("Технические характеристики"),
                technoparkParser.toCharacteristic("Видое разъемы"));
        assertEquals(new Characteristic().setName("Технические характеристики"),
                technoparkParser.toCharacteristic("Математический блок"));
        assertEquals(new Characteristic().setName("Технические характеристики"),
                technoparkParser.toCharacteristic("Поддержка стандартов"));
        assertEquals(new Characteristic().setName("Технические характеристики"),
                technoparkParser.toCharacteristic("Технические характеристики"));
        assertEquals(new Characteristic().setName("Разъемы"),
                technoparkParser.toCharacteristic("Разъемы"));
        assertEquals(new Characteristic().setName("Комплектация"),
                technoparkParser.toCharacteristic("Комплектация"));
        assertEquals(new Characteristic().setName("Размер и вес"),
                technoparkParser.toCharacteristic("Размер и вес"));
        assertEquals(new Characteristic().setName("Дополнительные характеристики"),
                technoparkParser.toCharacteristic("some characteristic"));
    }
}