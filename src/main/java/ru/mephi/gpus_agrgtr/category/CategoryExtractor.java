package ru.mephi.gpus_agrgtr.category;


import ru.mephi.gpus_agrgtr.entity.Category;

import java.util.Set;

public interface CategoryExtractor {
    String computeID(String name);

    Set<Category> extractCategorySet(String name);

    String extractProductCode(String name);
}
