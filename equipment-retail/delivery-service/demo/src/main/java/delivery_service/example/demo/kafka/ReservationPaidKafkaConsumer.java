package delivery_service.example.demo.kafka;

import delivery_service.example.demo.domain.DeliveryProcessor;
import delivery_service.example.demo.domain.DeliveryRepository;
import dev.common_libs.kafka.ReservationPaidEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@EnableKafka
@Configuration
@AllArgsConstructor
public class ReservationPaidKafkaConsumer {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryProcessor processor;

    @KafkaListener(
        topics ="${order-paid-topic}",
            containerFactory = "orderPaidEventListenerFactory"
    )
    public void listen(ReservationPaidEvent event){
        log.info("Received reservation paid event: {}", event);
        processor.processOrderPaidEvent(event);
    }

}
