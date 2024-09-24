package com.codenear.butterfly.product.application;

import com.codenear.butterfly.product.domain.Category;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Category::getValue)
                .collect(Collectors.toList());
    }
}