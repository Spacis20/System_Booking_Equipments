package equipment_service.example.demo.db.equipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long> {
}
