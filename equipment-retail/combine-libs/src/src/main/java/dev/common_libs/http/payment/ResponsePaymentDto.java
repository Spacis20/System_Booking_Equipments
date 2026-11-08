package dev.common_libs.http.payment;

import java.math.BigDecimal;

public record ResponsePaymentDto(
        Long id,
        BigDecimal amount,
        PaymentStatus status,
        PaymentMethod method
) {
}
