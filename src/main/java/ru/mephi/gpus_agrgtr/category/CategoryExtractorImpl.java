package ru.mephi.gpus_agrgtr.category;

import org.springframework.stereotype.Component;
import ru.mephi.gpus_agrgtr.entity.Category;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CategoryExtractorImpl implements CategoryExtractor {


    @Override
    public String computeID(String name) {
        return null;
    }

    @Override
    public Set<Category> extractCategorySet(String name) {
        if (name.contains("(")) {
            name = name.substring(0, name.indexOf("(") - 1);
        }
        String[] cat = name.split(" ");
        return Arrays.stream(cat).map(Category::new).collect(Collectors.toSet());
    }

    @Override
    public String extractProductCode(String name) {
        int firstBracketPos = name.indexOf("(");
        int secondBracketPos = name.indexOf(")");
        if (firstBracketPos != -1 || secondBracketPos != -1) {
            return name.substring(name.indexOf("(") + 1, name.indexOf(")"));
        } else return "";
    }

    @Override
    public Boolean AreEqual(Set<Category> first, Set<Category> second) {
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
