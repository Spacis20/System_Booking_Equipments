package equipment_service.example.demo.db.processors;

import equipment_service.example.demo.db.equipment.*;
import equipment_service.example.demo.dto.EquipmentMapper;
import equipment_service.example.demo.dto.equipment.CreateEquipmentRequestDto;
import equipment_service.example.demo.dto.equipment.EquipmentResponseDto;
import equipment_service.example.demo.dto.equipment.UpdateEquipmentRequestDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = false
)
public class EquipmentProcessor {

    private final EquipmentMapper mapper;
    private final EquipmentRepository repository;
    private final EquipmentCategoryRepository categoryRepository;

    public EquipmentResponseDto create(CreateEquipmentRequestDto requestDto){
        var entity = mapper.toEntity(requestDto);

        entity.setStatus(EquipmentStatus.ACTIVE);
        var category = categoryRepository.findById(requestDto.categoryId())
                        .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        entity.setEquipmentCategory(category);
        entity.setQuantity(requestDto.quantity());
        entity.setPricePerDay(requestDto.pricePerDay());
        var save = repository.save(entity);
        return mapper.toEquipmentDto(save);
    }

    @CacheEvict(
            value = "equipment",
            key = "#id"
    )
    public EquipmentResponseDto delete(Long id){
        var entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment wasn't found") );
        repository.delete(entity);
        return mapper.toEquipmentDto(entity);
    }
    @CacheEvict(
            value = "equipment",
            key = "#id"
    )
    public EquipmentResponseDto update(Long id, UpdateEquipmentRequestDto requestDto){
        var entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment wasn't found"));
        mapper.updateEquipment(requestDto, entity);
        return mapper.toEquipmentDto(entity);
    }

    @Cacheable(
            value = "equipment",
            key = "#id"
    )
    public EquipmentResponseDto getByid(Long id) {
        var entity =  repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment wasn't found"));
        return mapper.toEquipmentDto(entity);
    }

    @Cacheable(
            value = "equipment-list",
            key = "'all'"
    )
    public Set<EquipmentResponseDto> getAll(){
        Set<EquipmentResponseDto> set = new HashSet<>();
        var rep = repository.findAll();
        for (var s: rep){
            var obj = mapper.toEquipmentDto((EquipmentEntity) s);
            set.add(obj);
        }
        return set;
    }

    @CacheEvict(
            value = "equipment",
            key = "#id"
    )
    public void deleteAll() {
        repository.deleteAll();
    }
}
