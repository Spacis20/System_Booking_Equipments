package equipment_service.example.demo.db.equipment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Table(name = "equipmentCategory")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentCategory {

    @Id
    @Column(name = "categoryId", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nameCategory", unique = true)
    private String name;

    @Column(name = "descriptionCategory")
    private String description;

    @OneToMany(mappedBy = "equipmentCategory", cascade = CascadeType.PERSIST)
    private Set<EquipmentEntity> equipments = new LinkedHashSet<>();
}
