package equipment_service.example.demo.dto.error;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ErrorExeptionDto(
    String message,
    String detailMessage,
    LocalDateTime timeError
) {
}
