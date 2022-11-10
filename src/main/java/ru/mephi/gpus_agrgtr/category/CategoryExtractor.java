package ru.mephi.gpus_agrgtr.category;

import org.springframework.stereotype.Component;
import ru.mephi.gpus_agrgtr.entity.Category;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CategoryExtractor {

    static final int NUMBER_OF_EQUAL_CATEGORIES = 2;

    public Set<Category> extractCategorySet(String name) {
        name = getNameWithoutProductCode(name);
        String[] categories = name.split(" ");
        return Arrays.stream(categories)
                .map(Category::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private static String getNameWithoutProductCode(String name) {
        if (name.contains("(")) {
            name = name.substring(0, name.indexOf("(") - 1);
        }
        return name;
    }

    public String extractProductCode(String name) {
        int firstBracketPos = name.indexOf("(");
        int secondBracketPos = name.indexOf(")");
        if (firstBracketPos != -1 || secondBracketPos != -1) {
            return name.substring(firstBracketPos + 1, secondBracketPos);
        } else return "";
    }

    public Boolean isEqual(Set<Category> first, Set<Category> second) {
        Set<Category> largest = first.size() > second.size() ? first : second;
        Set<Category> smallest = first.size() > second.size() ? second : first;
        if (largest.size() - smallest.size() > NUMBER_OF_EQUAL_CATEGORIES)
            return false;
        int counter = 0;
        for (Category category : smallest) {
            if (largest.contains(category)) {
                counter++;
            }
        }
        return (largest.size() - counter) + (smallest.size() - counter) <= NUMBER_OF_EQUAL_CATEGORIES;
    }
}
