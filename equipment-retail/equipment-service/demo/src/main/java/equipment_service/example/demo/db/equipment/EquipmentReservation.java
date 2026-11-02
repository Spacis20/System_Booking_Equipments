package equipment_service.example.demo.db.equipment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Reservation")
public class EquipmentReservation {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservationId", nullable = false)
    private  Long id;

    @Column(name = "equipmentId")
    private Long equipmentId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReservationStatus status;

    @Column(name = "rentalOrderId")
    private Long rentalOrderId;
}
