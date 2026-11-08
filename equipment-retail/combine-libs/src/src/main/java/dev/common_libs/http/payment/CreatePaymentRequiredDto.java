package dev.common_libs.http.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreatePaymentRequiredDto(
        @NotNull
        Long reservationId,
        @Positive
        @NotNull
        BigDecimal amount,
        PaymentMethod method
) {
}
