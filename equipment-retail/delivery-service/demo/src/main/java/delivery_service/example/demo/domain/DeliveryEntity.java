package delivery_service.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@Table(name = "delivery")
@NoArgsConstructor
public class DeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orderId")
    private Long orderId;

    @Column(name = "nameCourier")
    private String nameCourier;

    @Column(name = "etaMinutes")
    private Integer etaMinutes;

}
