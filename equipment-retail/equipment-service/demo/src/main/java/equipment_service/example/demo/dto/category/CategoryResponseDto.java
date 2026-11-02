package equipment_service.example.demo.dto.category;

import lombok.Builder;

@Builder
public record CategoryResponseDto(
        Long id,
        String name,
        String description
) {
}
