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
                .collect(Collectors.toSet());
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
        int size1 = first.size();
        int size2 = second.size();
        int sizeDiff = size1 - size2;
        if (sizeDiff > NUMBER_OF_EQUAL_CATEGORIES || sizeDiff < -NUMBER_OF_EQUAL_CATEGORIES)
            return false;
        Set<Category> one = new HashSet<>(first);
        Set<Category> two = new HashSet<>(second);
        one.removeAll(second);
        two.removeAll(first);
        if (one.size() == 0 && two.size() == 0)
            return true;
        if (one.size() <= NUMBER_OF_EQUAL_CATEGORIES && two.size() == 0)
            return true;
        if (two.size() <= NUMBER_OF_EQUAL_CATEGORIES && one.size() == 0)
            return true;
        return false;
    }
}
