package ru.mephi.gpus_agrgtr.rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mephi.gpus_agrgtr.entity.Category;
import ru.mephi.gpus_agrgtr.rest.repositories.CategoryRepository;

import javax.transaction.Transactional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public void save(Set<Category> categories) {
        categories.forEach(category -> {
            if (!categoryRepository.existsByName(category.getName())) {
                categoryRepository.save(category);
            }
        });
    }
}
