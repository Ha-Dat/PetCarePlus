package org.example.petcareplus.service;

import org.example.petcareplus.entity.Payment;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {
    String createPaymentUrl(BigDecimal amount, Long orderId) throws Exception;

    Payment savePaymentFromVnPayReturn(Map<String, String> params);
}
