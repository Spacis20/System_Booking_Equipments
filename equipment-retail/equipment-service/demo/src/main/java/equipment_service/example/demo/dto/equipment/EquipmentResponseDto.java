package equipment_service.example.demo.dto.equipment;

import equipment_service.example.demo.db.equipment.EquipmentCategory;
import equipment_service.example.demo.db.equipment.EquipmentStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record EquipmentResponseDto(
        Long id,
        String name,
        String description,
        BigDecimal pricePerDay,
        Integer quantity,
        Integer availableQuantity,
        String categoryName,
        EquipmentStatus status
) {
}
