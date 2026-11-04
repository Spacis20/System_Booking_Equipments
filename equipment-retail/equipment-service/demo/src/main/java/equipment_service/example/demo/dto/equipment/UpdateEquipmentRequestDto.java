package equipment_service.example.demo.dto.equipment;

import equipment_service.example.demo.db.equipment.EquipmentCategory;
import equipment_service.example.demo.db.equipment.EquipmentStatus;

import java.math.BigDecimal;

public record UpdateEquipmentRequestDto(
        Long id,
        String name,
        String description,
        BigDecimal pricePerDay,
        Integer quantity,
        EquipmentCategory equipmentCategory,
        EquipmentStatus status
) {
}
