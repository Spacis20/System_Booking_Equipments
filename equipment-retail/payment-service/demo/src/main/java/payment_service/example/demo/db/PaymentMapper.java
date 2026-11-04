package payment_service.example.demo.db;

import org.mapstruct.*;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import dev.common_libs.http.payment.CreatePaymentRequiredDto;
import dev.common_libs.http.payment.ResponsePaymentDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
    PaymentEntity toEntity(CreatePaymentRequiredDto requiredDto);
    ResponsePaymentDto toPaymentDto(PaymentEntity entity);
}
