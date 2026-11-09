package equipment_service.example.demo.db.processors;

import dev.common_libs.http.payment.CreatePaymentRequiredDto;
import dev.common_libs.http.payment.PaymentStatus;
import dev.common_libs.http.payment.ResponsePaymentDto;
import dev.common_libs.kafka.DeliveryAssignedEvent;
import dev.common_libs.kafka.ReservationPaidEvent;
import equipment_service.example.demo.api.OrderPaymentRequest;
import equipment_service.example.demo.db.equipment.*;
import equipment_service.example.demo.dto.EquipmentMapper;
import equipment_service.example.demo.dto.reservation.CreateEquipmentReservationDto;
import equipment_service.example.demo.dto.reservation.ResponseReservationDto;
import equipment_service.example.demo.external.PaymentHttpClient;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class ReservationProcessor {

    private final EquipmentMapper mapper;
    private final ReservationRepository repositoryReservation;
    private final EquipmentRepository equipmentRepository;
    private final PaymentHttpClient paymentHttpClient;
    private final KafkaTemplate<Long, ReservationPaidEvent> kafkaTemplate;

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            readOnly = false
    )
    public ResponseReservationDto create(CreateEquipmentReservationDto reservationDto){
        log.info("Equipment Reservation CREATE is launched");
        var entity = mapper.toEntityReservation(reservationDto);
        var equipmentId = entity.getEquipmentId();
        var equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not founded"));

        if (entity.getQuantity() > equipment.getQuantity()){
            throw new IllegalArgumentException("Available equipment nor found");
        }

        var listReservations = findAllReservations(reservationDto);

        if (checkReservations(entity, equipment,listReservations)){
                entity.setStatus(ReservationStatus.WAIT_PAYMENT);
                var longDays = ChronoUnit.DAYS.between(entity.getStartDate(),entity.getEndDate());
                entity.setQuantityDaysRental(longDays);
                var save = repositoryReservation.save(entity);
                return mapper.toReservationDto(save);
            }
        throw new IllegalArgumentException("Equipment not available that dates");
    }

    private List<EquipmentReservation> findAllReservations(
            CreateEquipmentReservationDto reservationDto
    ){
        var statuses = List.of(ReservationStatus.CANCELLED, ReservationStatus.FAILED);
        return repositoryReservation.findByEquipmentIdAndStatusNotInAndStartDateLessThanAndEndDateGreaterThan(
                reservationDto.equipmentId(), statuses,
                reservationDto.endDate(), reservationDto.startDate()
        );
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
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            readOnly = false
    )
    public void delete(
            Long id
    ){
        log.info("Equipment Reservation DELETE is launched");
        repositoryReservation.deleteById(id);
    }

    @Transactional(
            readOnly = true
    )
    public ResponseReservationDto getReservation(Long id) {
        var reservation = repositoryReservation.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment reservation not founded"));
        return mapper.toReservationDto(reservation);
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            readOnly = false
    )
    public ResponseReservationDto cancel(Long id) {
        var entity = repositoryReservation.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation was not founded"));
        if (entity.getStartDate().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Reservation already running");
        }
        changeStatus(ReservationStatus.CANCELLED, entity);
        return mapper.toReservationDto(entity);
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            readOnly = false
    )
    public EquipmentReservation makePayment(Long id, OrderPaymentRequest request) {
        var entity = repositoryReservation.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Reservation not found for payment"));
        var equipment = equipmentRepository.findById(entity.getEquipmentId()).orElseThrow(
                () -> new EntityNotFoundException("Equipment not found by id: "
                        + entity.getEquipmentId())
        );

        log.info("Requaring to payment service with method: {}", request.method());
        var response = paymentHttpClient.createPayment(
                CreatePaymentRequiredDto.builder()
                        .reservationId(entity.getId())
                        .amount(BigDecimal.valueOf(
                                entity.getQuantityDaysRental()).multiply(equipment.getPricePerDay()))
                        .method(request.method())
                        .build());


        var status = response.status().equals(PaymentStatus.PAID_SUCCEED)
                ? changeStatus(ReservationStatus.PAID, entity)
                : changeStatus(ReservationStatus.FAILED, entity);

        var save = repositoryReservation.save(entity);
        if (status.equals(ReservationStatus.PAID)){
            sendDelivery(entity, response);
        }
        return save;

    }

    private void sendDelivery(EquipmentReservation entity,
                              ResponsePaymentDto response) {
        kafkaTemplate.send(
            orderPaidTopic,
            entity.getId(),
            ReservationPaidEvent.builder()
                    .reservationId(entity.getId())
                    .paymentId(response.id())
                    .amount(response.amount())
                    .paymentMethod(response.method())
                    .build()
        ).thenAccept( result ->
                log.info("Order paid event by id: {}", entity.getId()));
    }

    @Transactional(
           readOnly = true
    )
    public Set<ResponseReservationDto> getOrder() {
        return repositoryReservation
                .findAll()
                .stream()
                .map(mapper::toReservationDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            readOnly = false
    )
    public void deleteAll() {
        repositoryReservation.deleteAll();
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            readOnly = false
    )
    public void processDeliveryAssigned(DeliveryAssignedEvent event) {
        var order = repositoryReservation.findById(event.reservationId()).orElseThrow(
                () -> new EntityNotFoundException("Reservation don't found by id:"+ event.reservationId())
        );

        if (order.getStatus().equals(ReservationStatus.DELIVERED)){
            log.info("Order delivery already processed: id ={}", order.getId());
            return;
        }

        changeStatus(ReservationStatus.DELIVERED, order);

        order.setNameCourier(event.courierName());
        order.setEtaMinutes(event.etaMinutes());
        repositoryReservation.save(order);
        log.info("Equipment delivered to client by id: {}", order.getId());
    }


    private ReservationStatus changeStatus(ReservationStatus newStatus,
                             EquipmentReservation reservation){
        var resStatus = reservation.getStatus();
        if (!resStatus.toTransactionTo(newStatus)){
            throw new IllegalArgumentException("Impossible change status "
                    + resStatus + " to" + newStatus);
        }
        reservation.setStatus(newStatus);
        return newStatus;
    }
}
