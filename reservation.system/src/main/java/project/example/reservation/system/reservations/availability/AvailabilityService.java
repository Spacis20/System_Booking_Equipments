package project.example.reservation.system.reservations.availability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import project.example.reservation.system.reservations.ReservationRepository;
import project.example.reservation.system.reservations.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityService {

    private final ReservationRepository repository;
    private static final Logger log = LoggerFactory
            .getLogger(AvailabilityService.class);

    public AvailabilityService(ReservationRepository repository) {
        this.repository = repository;
    }

    public boolean isReservationAvailable(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ){

        if (!endDate.isAfter(startDate)){
            throw new IllegalArgumentException("Start date have to be later");
        }
        // Тут проверяем, что если из всего списка бронирований хотя бы что-то пересекается с входящей бронью
        List<Long> conflict = repository.findAllConflicts(
                roomId,
                startDate,
                endDate,
                ReservationStatus.APPROVED
        );
        if (conflict.isEmpty()){
            return true;
        }
        log.info("Conflict with id:" + conflict);
        return false;
    }
}
