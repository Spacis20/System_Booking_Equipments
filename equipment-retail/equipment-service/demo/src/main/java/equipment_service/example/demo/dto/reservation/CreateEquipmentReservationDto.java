package equipment_service.example.demo.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEquipmentReservationDto(
        Long equipmentId,
        Long rentalOrderId,
        LocalDate startDate,
        LocalDate endDate,
        Integer quantity
) {
}
