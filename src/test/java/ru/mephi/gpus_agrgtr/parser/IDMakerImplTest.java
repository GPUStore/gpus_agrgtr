package ru.mephi.gpus_agrgtr.parser;

import org.junit.jupiter.api.Test;
import ru.mephi.gpus_agrgtr.entity.Category;
import ru.mephi.gpus_agrgtr.category.CategoryExtractor;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class IDMakerImplTest {
    private final CategoryExtractor categoryExtractor = new CategoryExtractor();
    final String name = "GIGABYTE GEFORCE GTX1660TI 6GB (GV-N166TOC-6GD)";
    @Test
    void extractProductCode1() {
        assertEquals("GV-N166TOC-6GD", categoryExtractor.extractProductCode(name));
    }

    @Test
    void extractProductCode2() {
        assertEquals("", categoryExtractor.extractProductCode("GIGABYTE GEFORCE GTX1660TI 6GB"));
    }

    @Test
    void getCategoryList() {
        Set<Category> categorySet = categoryExtractor.extractCategorySet(name);

        Set<String> names = categorySet.stream()
                .map(Category::getName)
                .collect(Collectors.toSet());

        assertTrue(names.contains("GIGABYTE"));
        assertTrue(names.contains("GEFORCE"));
        assertTrue(names.contains("GTX1660TI"));
        assertTrue(names.contains("6GB"));
    }

    @Test
    void equalsTest1(){
        String name1 = "GIGABYTE GEFORCE GTX1660 SUPER 6GB (GV-N166SOC-6GD)";
        String name2 = "GIGABYTE GEFORCE GTX1660 6GB (GV-N1660OC-6GD)";

        Set<Category> categorySet1 = categoryExtractor.extractCategorySet(name1);
        Set<Category> categorySet2 = categoryExtractor.extractCategorySet(name2);

        assertTrue(categoryExtractor.isEqual(categorySet1,categorySet2));
    }
    @Test
    void equalsTest2(){
        String name1 = "ASUS NVIDIA GEFORCE GTX1660 SUPER 6GB (DUAL-GTX1660S-O6G-EVO)";
        String name2 = "ASUS NVIDIA GEFORCE GTX1660 SUPER 6GB (DUAL-GTX1660S-O6G-EVO)";

        Set<Category> categorySet1 = categoryExtractor.extractCategorySet(name1);
        Set<Category> categorySet2 = categoryExtractor.extractCategorySet(name2);

        assertTrue(categoryExtractor.isEqual(categorySet1,categorySet2));
    }

    @Test
    void equalsTest3(){
        String name2 = "GIGABYTE GEFORCE GTX1660 SUPER 6GB (GV-N166SOC-6GD)";
        String name1 = "GIGABYTE GEFORCE GTX1660 6GB (GV-N1660OC-6GD)";

        Set<Category> categorySet1 = categoryExtractor.extractCategorySet(name1);
        Set<Category> categorySet2 = categoryExtractor.extractCategorySet(name2);

        assertTrue(categoryExtractor.isEqual(categorySet1,categorySet2));
    }

    @Test
    void equalsTest4(){
        String name2 = "MSI GEFORCE GTX 1660 SUPER VENTUS XS OC RU 6GB";
        String name1 = "GIGABYTE GEFORCE GTX1660 6GB (GV-N1660OC-6GD)";

        Set<Category> categorySet1 = categoryExtractor.extractCategorySet(name1);
        Set<Category> categorySet2 = categoryExtractor.extractCategorySet(name2);

        assertFalse(categoryExtractor.isEqual(categorySet1,categorySet2));
    }

    @Test
    void equalsTest5(){
        String name2 = "MSI GEFORCE GTX 1660 ";
        String name1 = "GIGABYTE GEFORCE GTX1660 6GB (GV-N1660OC-6GD)";

        Set<Category> categorySet1 = categoryExtractor.extractCategorySet(name1);
        Set<Category> categorySet2 = categoryExtractor.extractCategorySet(name2);

        assertFalse(categoryExtractor.isEqual(categorySet1,categorySet2));
    }
}
