package dev.common_libs.kafka;

import lombok.Builder;

@Builder
public record DeliveryAssignedEvent(
        Long reservationId,
        String courierName,
        Integer etaMinutes
) {
}
