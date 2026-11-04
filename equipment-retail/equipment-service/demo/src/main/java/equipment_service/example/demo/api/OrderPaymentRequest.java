package equipment_service.example.demo.api;

import dev.common_libs.http.payment.PaymentMethod;

public record OrderPaymentRequest(
        PaymentMethod method
        ) {
}
