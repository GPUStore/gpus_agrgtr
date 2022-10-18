package ru.mephi.gpus_agrgtr.category;

import org.springframework.stereotype.Component;
import ru.mephi.gpus_agrgtr.entity.Category;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CategoryExtractor {

    public Set<Category> extractCategorySet(String name) {
        if (name.contains("(")) {
            name = name.substring(0, name.indexOf("(") - 1);
        }
        String[] categories = name.split(" ");
        return Arrays.stream(categories).map(Category::new).collect(Collectors.toSet());
    }

    public String extractProductCode(String name) {
        int firstBracketPos = name.indexOf("(");
        int secondBracketPos = name.indexOf(")");
        if (firstBracketPos != -1 || secondBracketPos != -1) {
            return name.substring(firstBracketPos + 1, secondBracketPos);
        } else return "";
    }

    public Boolean isEqual(Set<Category> first, Set<Category> second) {
        Set<Category> one = new HashSet<>(first);
        Set<Category> two = new HashSet<>(second);
        one.removeAll(second);
        two.removeAll(first);
        if (one.size() == 0 && two.size() == 0)
            return true;
        if (one.size() <= 2 && two.size() == 0)
            return true;
        if (two.size() <= 2 && one.size() == 0)
            return true;
        return false;
    }
}
