package equipment_service.example.demo.dto.equipment;

import equipment_service.example.demo.db.equipment.EquipmentCategory;

import java.math.BigDecimal;

public record CreateEquipmentRequestDto(
        String name,
        String description,
        BigDecimal pricePerDay,
        Integer quantity,
        Long categoryId
) {
}
