package equipment_service.example.demo.dto.reservation;

import equipment_service.example.demo.db.equipment.EquipmentStatus;
import equipment_service.example.demo.db.equipment.ReservationStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record ResponseReservationDto(
        Long id,
        Long rentalOrderId,
        Long equipmentId,
        Integer quantity,
        LocalDate startDate,
        LocalDate endDate,
        ReservationStatus status
) {
}
