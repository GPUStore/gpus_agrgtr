package ru.mephi.gpus_agrgtr.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.mephi.gpus_agrgtr.entity.Characteristic;
import ru.mephi.gpus_agrgtr.entity.Product;
import ru.mephi.gpus_agrgtr.parser.videocards.test.AbstractParserTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest extends AbstractParserTest {

    private Parser parser;

    @BeforeEach
    public void init() {
        parser = Mockito.spy(new Parser() {
            @Override
            public Characteristic toCharacteristic(String name) {
                return null;
            }

            @Override
            protected List<Product> getAllProducts() {
                return null;
            }
        });
    }

    @Test
    void parseTestWhenExceptionThrows() throws Exception {
        Mockito.when(parser.getAllProducts()).thenThrow(RuntimeException.class);

        List<Product> actual = parser.parse();

        assertEquals(0, actual.size());
    }

    @Test
    void getTestWithEmptyLink() {
        assertThrows(IllegalArgumentException.class, () -> parser.get(""));
    }

    @Test
    void postTestWithIncorrectLink() {
        assertThrows(RuntimeException.class, () -> parser.post("link", "body", Class.class));
    }

    @Test
    void postTestWithEmptyLink() {
        assertThrows(RuntimeException.class, () -> parser.post("", "body", Class.class));
    }
}