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
import jakarta.transaction.Transactional;
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
@Transactional
public class ReservationProcessor {

    private final EquipmentMapper mapper;
    private final ReservationRepository repositoryReservation;
    private final EquipmentRepository equipmentRepository;
    private final PaymentHttpClient paymentHttpClient;
    private final KafkaTemplate<Long, ReservationPaidEvent> kafkaTemplate;

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

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
                reservationDto.equipmentId(), ReservationStatus.WAIT_PAYMENT,
                reservationDto.endDate(), reservationDto.startDate()
        );
        if (checkReservations(entity, equipment,listReservations)){
                entity.setStatus(ReservationStatus.WAIT_PAYMENT);
                var longDays = ChronoUnit.DAYS.between(entity.getStartDate(),entity.getEndDate());
                entity.setQuantityDaysRental(longDays);
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

    public ResponseReservationDto cancel(Long id) {
        var entity = repositoryReservation.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation was not founded"));
        if (entity.getStartDate().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Reservation already running");
        }
        entity.setStatus(ReservationStatus.CANCELLED);
        return mapper.toReservationDto(entity);
    }

    public EquipmentReservation makePayment(Long id, OrderPaymentRequest request) {
        var entity = repositoryReservation.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Reservation not found for payment"));
        var equipment = equipmentRepository.findById(entity.getEquipmentId()).orElseThrow(
                () -> new EntityNotFoundException("Equipment not found by id: "
                        + entity.getEquipmentId())
        );

        if(!entity.getStatus().equals(ReservationStatus.WAIT_PAYMENT)){
            throw new IllegalArgumentException("Reservation Status must be RESERVED");
        }
        log.info("Requaring to payment service with method: {}", request.method());
        var response = paymentHttpClient.createPayment(
                CreatePaymentRequiredDto.builder()
                        .rentalOrderId(entity.getRentalOrderId())
                        .amount(BigDecimal.valueOf(
                                entity.getQuantityDaysRental()).multiply(equipment.getPricePerDay()))
                        .method(request.method())
                        .build());

        var status = response.status().equals(PaymentStatus.PAID_SUCCEED)
                ? ReservationStatus.PAID
                : ReservationStatus.FAILED;

        entity.setStatus(status);
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
                    .rentalOrderId(entity.getId())
                    .paymentId(response.id())
                    .amount(response.amount())
                    .paymentMethod(response.method())
                    .build()
        ).thenAccept( result ->
                log.info("Order paid event by id: {}", entity.getId()));
    }

    public Set<ResponseReservationDto> getOrder() {
        return repositoryReservation
                .findAll()
                .stream()
                .map(mapper::toReservationDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }


    public void deleteAll() {
        repositoryReservation.deleteAll();
    }

    public void processDeliveryAssigned(DeliveryAssignedEvent event) {
        var order = repositoryReservation.findById(event.orderId()).orElseThrow(
                () -> new EntityNotFoundException("Reservation don't found by id:"+ event.orderId())
        );
        if (!order.getStatus().equals(ReservationStatus.PAID)){
            if (order.getStatus().equals(ReservationStatus.DELIVERED)){
                log.info("Order delivery already processed: id ={}", order.getId());
            }
            log.error("Trying to assigned delivery but Order have incorrect state id={}", order.getId());
            return;
        }
        order.setStatus(ReservationStatus.DELIVERED);
        order.setNameCourier(event.courierName());
        order.setEtaMinutes(event.etaMinutes());
        repositoryReservation.save(order);
        log.info("Equipment delivered to client by id: {}", order.getId());
    }
}
