package delivery_service.example.demo.domain;

import dev.common_libs.kafka.DeliveryAssignedEvent;
import dev.common_libs.kafka.ReservationPaidEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryProcessor {

    private final DeliveryRepository deliveryRepository;
    private final KafkaTemplate<Long, DeliveryAssignedEvent> kafkaTemplate;

    @org.springframework.beans.factory.annotation.Value("${delivery-assigned-topic}")
    private String deliveryTopic;

    public void processOrderPaidEvent(ReservationPaidEvent event) {
        var reservationId =event.reservationId();
        var found = deliveryRepository.findById(reservationId);
        if (found.isPresent()){
            log.info("Found order delivery already assigned: delivery: {}", found.get());
            return;
        }
        var assigned = assignedDelivery(reservationId);

        sendDeliveryAssignedEvent(assigned);
    }

    public void sendDeliveryAssignedEvent(DeliveryEntity assigned){
        kafkaTemplate.send(
                deliveryTopic,
                assigned.getReservationId(),
                DeliveryAssignedEvent.builder()
                        .courierName(assigned.getNameCourier())
                        .reservationId(assigned.getReservationId())
                        .etaMinutes(assigned.getEtaMinutes())
                        .build()
        ).thenAccept(result ->
                log.info("Delivery Assigned Event send: delivery id: {}", assigned.getId()));
    }

    public DeliveryEntity assignedDelivery(Long reservationId){
        var entity = new DeliveryEntity();
        entity.setReservationId(reservationId);
        entity.setNameCourier("Vladimir");
        entity.setEtaMinutes(13);
        deliveryRepository.save(entity);
        log.info("Delivery was successful: delivery: {}", entity);
        return entity;
    }

}
