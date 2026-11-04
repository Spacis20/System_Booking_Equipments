package payment_service.example.demo.db;

import dev.common_libs.http.payment.PaymentMethod;
import dev.common_libs.http.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentId")
    private Long id;

    @Column(name = "rentalOrderId", nullable = false)
    private Long rentalOrderId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentMethod", nullable = false)
    private PaymentMethod method;
}
