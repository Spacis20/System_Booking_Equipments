package equipment_service.example.demo.db.equipment;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@Table(name = "equipment")
@Setter
@Getter
@NoArgsConstructor
public class EquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipmentId", nullable = false)
    private Long id;

    @Column(name = "nameEquipment")
    private String name;

    @Column(name = "descriptionEqipment")
    private String description;

    @Column(name = "pricePerDay", nullable = false)
    private BigDecimal pricePerDay;

    @Column(name = "quantityEquipment", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipmentCategoryId")
    private EquipmentCategory equipmentCategory;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EquipmentStatus status;

}
