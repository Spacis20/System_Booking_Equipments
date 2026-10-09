package project.example.reservation.system.web;

import java.time.LocalDateTime;

public record ErrorExeptionDTO(
        String message,
        String detailMessage,  // error message
        LocalDateTime timeError
) {
}
