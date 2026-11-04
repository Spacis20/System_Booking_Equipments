package dev.common_libs.http.payment;

import java.math.BigDecimal;

public record ResponsePaymentDto(
        Long id,
        Long rentalOrderId,
        BigDecimal amount,
        PaymentStatus status,
        PaymentMethod method
) {
}
