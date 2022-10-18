package ru.mephi.gpus_agrgtr.parser;

import org.junit.jupiter.api.Test;
import ru.mephi.gpus_agrgtr.entity.Category;
import ru.mephi.gpus_agrgtr.category.CategoryExtractor;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class IDMakerImplTest {


    private CategoryExtractor categoryExtractor = new CategoryExtractor();
    final String name = "GIGABYTE GEFORCE GTX1660TI 6GB (GV-N166TOC-6GD)";

    @Test
    void extractProductCode() {
        assertEquals("GV-N166TOC-6GD", categoryExtractor.extractProductCode(name));
    }

    @Test
    void getCategoryList() {
        Set<Category> categorySet = categoryExtractor.extractCategorySet(name);
        Set<String> names = categorySet.stream().map(Category::getName).collect(Collectors.toSet());
        assertTrue(names.contains("GIGABYTE"));
        assertTrue(names.contains("GEFORCE"));
        assertTrue(names.contains("GTX1660TI"));
        assertTrue(names.contains("6GB"));
    }

    @Test
    void getId() {
    }
}