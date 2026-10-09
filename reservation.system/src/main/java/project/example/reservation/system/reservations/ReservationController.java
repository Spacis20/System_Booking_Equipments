package project.example.reservation.system.reservations;

//import org.aspectj.weaver.ast.Not;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    String path1 = "/reservation";

    private static final Logger log =
            LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;
    private Long userId;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> findReservation(
            @PathVariable("id") Long id
    ) throws Exception{
            log.info("called findReservation by id: "+ id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(reservationService.getReservationById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Reservation>> getAllReservations(
            @RequestParam(name = "roomId", required = false) Long roomId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam("pageNumber") Integer pageNumber
    ){
        var filter = new ReservationSearchFilter(
                roomId,
                userId,
                pageSize,
                pageNumber
        );
        log.info("Called getAllReservation");
        return ResponseEntity.ok(reservationService.searchAllByFilter(filter));
    }

    @PostMapping()
    public ResponseEntity<Reservation> createReservation(
            @RequestBody @Valid Reservation toReservation
    ){
        log.info("Called createReservation");
        return ResponseEntity.status(201)
                .header("test_header","123")
                .body(reservationService.createReservation(toReservation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable("id") Long id,
            @RequestBody @Valid Reservation reservation
    ) throws Exception{
        log.info("Called updateMapping, id = {}, reservation = {}", id, reservation);
            return ResponseEntity.ok(reservationService.putReservation(id, reservation));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id) throws Exception{
            log.info("Called delete reservation id={}", id);
            reservationService.cancelReservation(id);
            return ResponseEntity
                    .ok()
                    .build();
        }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
            @PathVariable("id") Long id
    )  throws Exception{
        log.info("Called approveReservation by id = {}", id);
        var reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }
}
