package equipment_service.example.demo.dto.reservation;


import equipment_service.example.demo.validation.ValidReservationDates;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@ValidReservationDates
public record CreateEquipmentReservationDto(
        @NotNull
        Long equipmentId,
        @FutureOrPresent
        LocalDate startDate,
        @FutureOrPresent
        LocalDate endDate,
        @Positive
        @NotBlank
        Integer quantity
) {
}
