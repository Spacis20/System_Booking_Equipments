package equipment_service.example.demo.db.processors;

import equipment_service.example.demo.db.equipment.*;
import equipment_service.example.demo.dto.EquipmentMapper;
import equipment_service.example.demo.dto.reservation.CreateEquipmentReservationDto;
import equipment_service.example.demo.dto.reservation.ResponseReservationDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class ReservationProcessor {

    private final EquipmentMapper mapper;
    private final ReservationRepository repositoryReservation;
    private final EquipmentRepository equipmentRepository;

    public ResponseReservationDto create(CreateEquipmentReservationDto reservationDto){
        log.info("Equipment Reservation CREATE is launched");
        var entity = mapper.toEntityReservation(reservationDto);
        var equipmentId = entity.getEquipmentId();
        var equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not founded"));

        if (entity.getQuantity() > equipment.getQuantity()){
            throw new IllegalArgumentException("Available equipment nor found");
        }

        var listReservations = repositoryReservation.findByEquipmentIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
                reservationDto.equipmentId(), ReservationStatus.RESERVED,
                reservationDto.endDate(), reservationDto.startDate()
        );
        if (checkReservations(entity, equipment,listReservations)){
                entity.setStatus(ReservationStatus.WAIT_PAYMENT);
                var save = repositoryReservation.save(entity);
                return mapper.toReservationDto(save);
            }
        throw new IllegalArgumentException("Equipment not available that dates");
    }

    private boolean checkReservations(
            EquipmentReservation entity,
            EquipmentEntity equipment,
            List<EquipmentReservation> listReservations
    ){
        TreeMap<LocalDate, Integer> mapReservation = new TreeMap<>();
        LocalDate start = entity.getStartDate();
        LocalDate end = entity.getEndDate();
        for (EquipmentReservation res : listReservations){
            start = entity.getStartDate().isBefore(res.getStartDate())
                    ? res.getStartDate()
                    : entity.getStartDate();
            end = entity.getEndDate().isAfter(res.getEndDate())
                    ? res.getEndDate()
                    : entity.getEndDate();
            mapReservation.merge(start, res.getQuantity(), Integer::sum);
            mapReservation.merge(end, -res.getQuantity(), Integer::sum);
        }
        int reserved = 0;
        for (Integer event : mapReservation.values()){
            reserved += event;
            int available = equipment.getQuantity() - reserved;
            if (available < entity.getQuantity()){
                throw new IllegalArgumentException("Equipment not available that dates");
            }
        }
        return true;
    }

    public void delete(
            Long id
    ){
        log.info("Equipment Reservation DELETE is launched");
        repositoryReservation.deleteById(id);
    }

    public ResponseReservationDto getReservation(Long id) {
        var reservation = repositoryReservation.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment reservation not founded"));
        return mapper.toReservationDto(reservation);
    }

    public Set<ResponseReservationDto> getAllReservations() {
        Set<ResponseReservationDto> setReservations = new HashSet<>();
        for(EquipmentReservation res : repositoryReservation.findAll()){
            var res_ = mapper.toReservationDto(res);
            setReservations.add(res_);
        }
        return setReservations;
    }

    public ResponseReservationDto cancel(Long id) {
        var entity = repositoryReservation.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation was not founded"));
        if (entity.getStartDate().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Reservation already running");
        }
        entity.setStatus(ReservationStatus.CANCELLED);
        return mapper.toReservationDto(entity);
    }
}
