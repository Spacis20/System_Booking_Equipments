package dev.common_libs.http.payment;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreatePaymentRequiredDto(
        Long rentalOrderId,
        BigDecimal amount,
        PaymentMethod method
) {
}
