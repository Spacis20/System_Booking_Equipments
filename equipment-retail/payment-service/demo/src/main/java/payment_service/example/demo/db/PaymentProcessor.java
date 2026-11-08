package payment_service.example.demo.db;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import dev.common_libs.http.payment.CreatePaymentRequiredDto;

import dev.common_libs.http.payment.*;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentProcessor {

    private final PaymentMapper mapper;
    private final PaymentRepository repository;

    public PaymentEntity makePayment(CreatePaymentRequiredDto requiredDto) {
        var request = repository.findById(requiredDto.reservationId());

        if (request.isPresent()){
            log.info("Payment already exist: {}", request.get());
            return request.get();
        }
        var entity = mapper.toEntity(requiredDto);

        var status = entity.getMethod().equals(PaymentMethod.QR)
                ? PaymentStatus.FAILED
                : PaymentStatus.PAID_SUCCEED;

        entity.setStatus(status);
        return repository.save(entity);
    }
}
