package equipment_service.example.demo.db.equipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<EquipmentReservation, Long> {

    List<EquipmentReservation> findByEquipmentIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
            Long equipmentId,
            ReservationStatus status,
            LocalDate requestedEnd,
            LocalDate requestedStart
    );
}
