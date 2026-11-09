package delivery_service.example.demo.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Long> {
    Optional<DeliveryEntity> findByReservationId(Long aLong);
}
