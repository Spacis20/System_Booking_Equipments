package equipment_service.example.demo.api;

import equipment_service.example.demo.db.equipment.EquipmentEntity;
import equipment_service.example.demo.db.equipment.EquipmentReservation;
import equipment_service.example.demo.db.processors.ReservationProcessor;
import equipment_service.example.demo.dto.EquipmentMapper;
import equipment_service.example.demo.dto.reservation.CreateEquipmentReservationDto;
import equipment_service.example.demo.dto.reservation.ResponseReservationDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/reservations")
public class ReservationController {

    private final EquipmentMapper mapper;
    private final ReservationProcessor processor;

    @PostMapping
    public ResponseReservationDto create(
            @RequestBody CreateEquipmentReservationDto reservationDto
            ){
        return processor.create(reservationDto);
    }

    @PostMapping("/delete/{id}")
    public void delete(
            @PathVariable Long id
    ){
        processor.delete(id);
    }

    @GetMapping("/{id}")
    public ResponseReservationDto getReservation(
            @PathVariable Long id
    ){
        log.info("Get reservation by id={}", id);
        return processor.getReservation(id);
    }

    @GetMapping
    public Set<ResponseReservationDto> getAllReservation(){
        log.info("Get all reservations");
        return processor.getAllReservations();
    }

    @PostMapping("/cancel/{id}")
    public ResponseReservationDto cancel(
            @PathVariable Long id
    ){
        log.info("Reservation is cancel by id: {}", id);
        return processor.cancel(id);
    }

}
