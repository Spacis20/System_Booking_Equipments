package equipment_service.example.demo.kafka;

import dev.common_libs.kafka.DeliveryAssignedEvent;
import equipment_service.example.demo.db.processors.ReservationProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@AllArgsConstructor
@EnableKafka
@Configuration
public class DeliveryAssignedKafkaConsumer {

    private final ReservationProcessor processor;

    @KafkaListener(
            topics = "${delivery-topic}",
            containerFactory = "deliveryAssignedEventEventListenerFactory"
    )
    public void listen(DeliveryAssignedEvent event) {

        log.info("Recieved delivery assigned event={}", event);

        processor.processDeliveryAssigned(event);
    }
}
