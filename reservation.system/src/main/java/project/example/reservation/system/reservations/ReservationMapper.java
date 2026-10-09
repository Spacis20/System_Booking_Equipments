package project.example.reservation.system.reservations;

import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public Reservation toDomain(ReservationEntity reservation){
        return new Reservation(
                reservation.getId(),
                reservation.getUser_id(),
                reservation.getRoom_id(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus()
        );
    }

    public ReservationEntity toEntity(Reservation reservation){
        return new ReservationEntity(
                reservation.id(),
                reservation.user_id(),
                reservation.room_id(),
                reservation.startDate(),
                reservation.endDate(),
                reservation.status()
        );
    }

}
