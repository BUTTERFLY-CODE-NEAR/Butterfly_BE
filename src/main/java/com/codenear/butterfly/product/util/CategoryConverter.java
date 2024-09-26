package com.codenear.butterfly.product.util;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.exception.ProductException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class CategoryConverter implements AttributeConverter<Category, String> {

    @Override
    public String convertToDatabaseColumn(Category category) {
        if (category == null) {
            return null;
        }
        return category.getValue();
    }

    @Override
    public Category convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }

        return Stream.of(Category.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new ProductException(ErrorCode.SERVER_ERROR, null));
    }
}