package com.codenear.butterfly.notify;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class NotifyConverter implements AttributeConverter<NotifyMessage, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final NotifyMessage notifyMessage) {
        return notifyMessage.getId();
    }

    @Override
    public NotifyMessage convertToEntityAttribute(final Integer code) {
        return NotifyMessage.findByCode(code);
    }
}
