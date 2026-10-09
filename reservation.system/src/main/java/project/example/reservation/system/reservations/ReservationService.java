package project.example.reservation.system.reservations;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.example.reservation.system.reservations.availability.AvailabilityService;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationMapper reservationMapper;
    private final ReservationRepository repository;
    private final AvailabilityService availabilityService;

    private final Logger log = LoggerFactory.getLogger(ReservationService.class);

    public ReservationService(ReservationMapper reservationMapper, ReservationRepository repository, AvailabilityService availabilityService) {
        this.reservationMapper = reservationMapper;
        this.repository = repository;
        this.availabilityService = availabilityService;
    }

    public List<Reservation> searchAllByFilter(
            ReservationSearchFilter filter
    ) {
        int pageSize = filter.pageSize() != null
                ? filter.pageSize() : 10;
        int pageNumber = filter.pageNumber() != null
                ? filter.pageNumber() : 0;

        var pageable = Pageable
                .ofSize(pageSize)
                .withPage(pageNumber);

        List<ReservationEntity> allreseptions = repository.searchAllByFilter(
                filter.roomId(), filter.userId(), pageable
        );

        return allreseptions.stream().map(
                reservationMapper::toDomain)
                .toList();
    }

    public Reservation getReservationById(Long id) throws NoSuchFieldException {

        ReservationEntity reservation = repository.findById(id)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("Not found resevation by id: " + id);
                });
        return reservationMapper.toDomain(reservation);
            }

    public Reservation createReservation (Reservation toReservation){

            if (toReservation.status() != null) {
                throw new IllegalArgumentException("Status should be empty");
            }
            if (!toReservation.endDate().isAfter(toReservation.startDate())){
                throw new IllegalArgumentException("Start date have to later");
            }
            var entityToSave = reservationMapper.toEntity(toReservation);
            entityToSave.setStatus(ReservationStatus.PENDING);

            var savedEntity = repository.save(entityToSave);
            return reservationMapper.toDomain(savedEntity);
        }

    public Reservation putReservation (Long id, Reservation updateReservation) throws NoSuchFieldException {
            var oldReservation = repository.findById(id)
                    .orElseThrow(() ->
                        new NoSuchFieldException("Reservation not founded")
                    );
            if (!oldReservation.getStatus().equals(ReservationStatus.PENDING)) {
                throw new IllegalStateException("Can't modify reservation: status: "
                        + oldReservation.getStatus());
            }

            if (!updateReservation.endDate().isAfter(updateReservation.startDate())){
                throw new IllegalArgumentException("Start date have to later");
            }

            var newReservation = reservationMapper.toEntity(updateReservation);
            newReservation.setId(oldReservation.getId());
            newReservation.setStatus(ReservationStatus.PENDING);

            var result = repository.save(newReservation);
            return reservationMapper.toDomain(result);
        }

    @Transactional
    public void cancelReservation(Long id) throws NoSuchFieldException {

            var reservation = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Nor found reservation id=" + id));

            if(reservation.getStatus().equals(ReservationStatus.APPROVED)){
                throw new IllegalStateException("Can't cancel approved reservation");
            }
            if(reservation.getStatus().equals(ReservationStatus.CANCELLED)){
                throw new IllegalStateException("Can't cancel cancelled reservation");
            }

            repository.setStatus(id, ReservationStatus.CANCELLED);
            log.info("Sucessfully reservation id={} was canceled", id);
        }

    public Reservation approveReservation (Long id) throws NoSuchFieldException {

            var approveReservation = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Not found by id: " + id));

            if (approveReservation.getStatus() != ReservationStatus.PENDING) {
                throw new IllegalStateException("Can't approve reservation room, status: "
                        + approveReservation.getStatus());
            }
            var isAvailable = availabilityService.isReservationAvailable(
                    approveReservation.getRoom_id(),
                    approveReservation.getStartDate(),
                    approveReservation.getEndDate()
            );
            if (!isAvailable) {
                throw new IllegalStateException("Can't approve reservation room due to conflict");
            }

            approveReservation.setStatus(ReservationStatus.APPROVED);

            var result = repository.save(approveReservation);
            return reservationMapper.toDomain(result);
        }



}
