package equipment_service.example.demo.validation;

import equipment_service.example.demo.dto.reservation.CreateEquipmentReservationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReservationDatesValidator implements ConstraintValidator<
        ValidReservationDates, CreateEquipmentReservationDto> {

    @Override
    public boolean isValid(CreateEquipmentReservationDto dto,
                           ConstraintValidatorContext context) {

        if (dto == null){
            return true;
        }

        if (dto.endDate() == null || dto.startDate() == null){
            return true;
        }

        return  dto.endDate().isAfter(dto.startDate());
    }
}
