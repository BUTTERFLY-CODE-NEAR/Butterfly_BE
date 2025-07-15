package com.codenear.butterfly.sms.domain.dto;

import com.google.api.client.util.DateTime;

public record CloudSmsResponseDTO(String requestId,
                                  DateTime requestTime,
                                  String statusCode,
                                  String statusName) {

}
