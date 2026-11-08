package equipment_service.example.demo.dto.reservation;

import equipment_service.example.demo.db.equipment.ReservationStatus;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ResponseReservationDto(
        Long id,
        Long equipmentId,
        Integer quantity,
        LocalDate startDate,
        LocalDate endDate,
        ReservationStatus status,
        Long quantityDaysRental
) {
}
