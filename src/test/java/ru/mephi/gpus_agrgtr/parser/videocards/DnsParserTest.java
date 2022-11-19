package ru.mephi.gpus_agrgtr.parser.videocards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import ru.mephi.gpus_agrgtr.entity.Characteristic;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.parser.videocards.dns.DnsParser;
import ru.mephi.gpus_agrgtr.parser.videocards.dns.enumerations.HtmlClasses;
import ru.mephi.gpus_agrgtr.parser.videocards.test.AbstractParserTest;
import ru.mephi.gpus_agrgtr.parser.videocards.test.dns.TestDataDns;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing DnsParser functionality.")
public class DnsParserTest extends AbstractParserTest {
    private static final String TEST_URL_FOR_INCORRECT_HTML = "src/test/repository/dns/dnsIncorrectList.html";
    private static final String TEST_URL_FOR_CORRECT_HTML = "src/test/repository/dns/dnsCorrectList.html";
    private static final String TEST_URL_FOR_CORRECT_DATA_1 = "src/test/repository/dns/dnsCorrectVideocard1.html";
    private static final String TEST_URL_FOR_CORRECT_DATA_2 = "src/test/repository/dns/dnsCorrectVideocard2.html";

    @InjectMocks
    DnsParser dnsParserSpy;
    @Mock
    WebDriver webDriver;

    @BeforeEach
    public void init() {
        DnsParser dnsParser = new DnsParser("", "", "store", null, webDriver);
        dnsParserSpy = Mockito.spy(dnsParser);
    }

    @Test
    @DisplayName("Проверка выдачи всех продуктов")
    void getAllProductsTest() {
        List<Product> expectedProducts = TestDataDns.getTestProducts();

        Mockito.doAnswer(new Answer() {
            private int count = 0;
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return switch (count++){
                    case 1-> getTestDocument(TEST_URL_FOR_CORRECT_DATA_1).toString();
                    case 2 -> getTestDocument(TEST_URL_FOR_CORRECT_DATA_2).toString();
                    default -> getTestDocument(TEST_URL_FOR_CORRECT_HTML).toString();
                };

            }
        }).when(webDriver).getPageSource();
        Mockito.doNothing()
                .when(dnsParserSpy).checkDownloaded(List.of(HtmlClasses.SPECIFICATION.getName()));
        Mockito.doReturn(new Characteristic().setName("characteristic1"))
                .when(dnsParserSpy).toCharacteristic("Основные параметры");
        Mockito.doReturn(new Characteristic().setName("characteristic2"))
                .when(dnsParserSpy).toCharacteristic("Спецификации видеопамяти");

        List<Product> actualProducts = dnsParserSpy.getAllProducts();

        assertEquals(expectedProducts.size(), actualProducts.size());
        assertProducts(expectedProducts.get(0), actualProducts.get(0));
        assertProducts(expectedProducts.get(1), actualProducts.get(1));
    }

    @Test
    void getAllProductsTestWithIncorrectUrlHTML() {
        assertThrows(
                RuntimeException.class,
                () -> dnsParserSpy.getAllProducts(),
                "The page was not retrieved, probably the wrong url"
        );
    }

    @Test
    void getAllProductsTestWithNotEqualsSizeLists() throws IOException {
        Mockito.doReturn(getTestDocument(TEST_URL_FOR_INCORRECT_HTML).toString())
                .when(webDriver).getPageSource();
        assertThrows(
                RuntimeException.class,
                () -> dnsParserSpy.getAllProducts(),
                "Website data integrity error."
        );
    }
    @Test
    void getCorrectNameTest() {
        String name1 =  "KFA2 GEFORCE GTX 1630 [63NQL4HP66EK]" +
                " [PCI-E, 4  GDDR6, 64 , DVI-D, DISPLAYPORT, HDMI, GPU 1800 ]";
        String name2 =  "PALIT GEFORCE GT 730 SILENT LP [NEAT7300HD46-2080H]" +
                " [PCI-E 2.0, 2  GDDR3, 64 , DVI-D, HDMI, VGA (D-SUB), GPU 902 ]";
        String name3 = "PALIT GEFORCE GT 730 SILENT LP";
        String name1Expected = "KFA2 GEFORCE GTX 1630 (63NQL4HP66EK)";
        String name2Expected = "PALIT GEFORCE GT 730 SILENT LP (NEAT7300HD46-2080H)";
        String name3Expected = "PALIT GEFORCE GT 730 SILENT LP";

        String name1Actual = DnsParser.getReplacedName(name1);
        String name2Actual = DnsParser.getReplacedName(name2);
        String name3Actual = DnsParser.getReplacedName(name3);

        assertEquals(name1Expected,name1Actual);
        assertEquals(name2Expected,name2Actual);
        assertEquals(name3Expected,name3Actual);
    }

    @Test
    void toCharacteristicTest() {
        assertEquals(new Characteristic().setName("Основные характеристики"),
                dnsParserSpy.toCharacteristic("Общие параметры" ));
        assertEquals(new Characteristic().setName("Основные характеристики"),
                dnsParserSpy.toCharacteristic("Основные параметры"));
        assertEquals(new Characteristic().setName("Технические характеристики"),
                dnsParserSpy.toCharacteristic("Вывод изображения" ));
        assertEquals(new Characteristic().setName("Технические характеристики"),
                dnsParserSpy.toCharacteristic("Спецификации видеопроцессора" ));
        assertEquals(new Characteristic().setName( "Технические характеристики"),
                dnsParserSpy.toCharacteristic("Спецификации видеопамяти"));
        assertEquals(new Characteristic().setName( "Разъемы"),
                dnsParserSpy.toCharacteristic("Подключение"));
        assertEquals(new Characteristic().setName( "Комплектация"),
                dnsParserSpy.toCharacteristic("Система охлаждения"));
        assertEquals(new Characteristic().setName("Размер и вес"),
                dnsParserSpy.toCharacteristic("Габариты и вес" ));
        assertEquals(new Characteristic().setName("Дополнительные характеристики"),
                dnsParserSpy.toCharacteristic("some characteristic"));
    }
}
