package dev.common_libs.kafka;

import dev.common_libs.http.payment.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReservationPaidEvent(
        Long reservationId,
        Long paymentId,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {
}