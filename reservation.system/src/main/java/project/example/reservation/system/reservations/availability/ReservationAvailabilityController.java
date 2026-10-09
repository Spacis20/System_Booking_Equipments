package project.example.reservation.system.reservations.availability;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reservation/availability")
public class ReservationAvailabilityController {

    private final AvailabilityService service;

    private static final Logger log = LoggerFactory
            .getLogger(ReservationAvailabilityController.class);

    public ReservationAvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    @PostMapping("/check")
    public ResponseEntity<CheckAvailabilityResponse> checkAvailability(
            @Valid CheckAvailabilityRequest request
    ){
        log.info("Called method checkAvailability: request={}", request);
        boolean isAvailable = service.isReservationAvailable(
                request.roomId(), request.startDate(), request.endDate()
        );
        var message = isAvailable
                ? "Room availabale to Reservation"
                : "Not room availabale to Reservation";
        var status = isAvailable
                ? AvailabilityStatus.AVAILABLE
                : AvailabilityStatus.RESERVED;
        return ResponseEntity.status(HttpStatus.OK)
            .body( new CheckAvailabilityResponse(message, status));
    }
}
