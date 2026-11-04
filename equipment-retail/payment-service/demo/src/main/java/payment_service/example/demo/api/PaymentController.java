package payment_service.example.demo.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payment_service.example.demo.db.PaymentMapper;
import payment_service.example.demo.db.PaymentProcessor;
import payment_service.example.demo.db.PaymentRepository;
import dev.common_libs.http.payment.CreatePaymentRequiredDto;
import dev.common_libs.http.payment.ResponsePaymentDto;

@RestController
@RequestMapping("/api/payment")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository repository;
    private final PaymentProcessor paymentProcessor;
    private final PaymentMapper mapper;

    @PostMapping
    public ResponsePaymentDto create(
            @RequestBody CreatePaymentRequiredDto requiredDto
            ){
        log.info("Recieved request: payment_request: {}", requiredDto);
        var payment = paymentProcessor.makePayment(requiredDto);
        return mapper.toPaymentDto(payment);
    }

}
