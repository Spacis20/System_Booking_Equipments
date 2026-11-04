package equipment_service.example.demo.external;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import dev.common_libs.http.payment.*;

@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "api/payment"
)
public interface PaymentHttpClient {

    @PostExchange
    ResponsePaymentDto createPayment(
            @RequestBody CreatePaymentRequiredDto requestDto);

}
