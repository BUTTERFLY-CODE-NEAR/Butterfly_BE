package com.codenear.butterfly.payment.application;

import com.codenear.butterfly.payment.domain.OrderDetails;
import com.codenear.butterfly.payment.domain.dto.request.CancelRequestDTO;

public interface PaymentCancel {
    void cancel(final CancelRequestDTO cancelRequestDTO, final OrderDetails orderDetails);

    String getProvider();
}
