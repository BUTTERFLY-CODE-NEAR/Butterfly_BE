package com.codenear.butterfly.notify.alarm.domain;

import com.codenear.butterfly.notify.NotifyConverter;
import com.codenear.butterfly.notify.NotifyMessage;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = NotifyConverter.class)
    private NotifyMessage notifyMessage;

    private boolean isNew;
}
