package equipment_service.example.demo.db.processors;

import equipment_service.example.demo.db.equipment.*;
import equipment_service.example.demo.dto.EquipmentMapper;
import equipment_service.example.demo.dto.equipment.CreateEquipmentRequestDto;
import equipment_service.example.demo.dto.equipment.EquipmentResponseDto;
import equipment_service.example.demo.dto.equipment.UpdateEquipmentRequestDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class EquipmentProcessor {

    private final EquipmentMapper mapper;
    private final EquipmentRepository repository;
    private final EquipmentCategoryRepository categoryRepository;

    public EquipmentResponseDto create(CreateEquipmentRequestDto requestDto){
        var entity = mapper.toEntity(requestDto);
//        if (repository.find(entity.getId()).isPresent()) {
//            log.info("Equipment already exist by id={}", entity.getId());
//            return mapper.toEquipmentDto(entity);
//        }
        entity.setStatus(EquipmentStatus.ACTIVE);
        var category = categoryRepository.findById(requestDto.categoryId())
                        .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        entity.setEquipmentCategory(category);
        entity.setQuantity(requestDto.quantity());
        entity.setPricePerDay(requestDto.pricePerDay());
        var save = repository.save(entity);
        return mapper.toEquipmentDto(save);
    }

    public EquipmentResponseDto delete(Long id){
        var entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment wasn't found") );
        repository.delete(entity);
        return mapper.toEquipmentDto(entity);
    }

    public EquipmentResponseDto update(Long id, UpdateEquipmentRequestDto requestDto){
        var entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment wasn't found"));
        mapper.updateEquipment(requestDto, entity);
        return mapper.toEquipmentDto(entity);
    }

    public EquipmentResponseDto getByid(Long id) {
        var entity =  repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment wasn't found"));
        return mapper.toEquipmentDto(entity);
    }

    public Set<EquipmentResponseDto> getAll(){
        Set<EquipmentResponseDto> set = new HashSet<>();
        var rep = repository.findAll();
        for (var s: rep){
            var obj = mapper.toEquipmentDto((EquipmentEntity) s);
            set.add(obj);
        }
        return set;
    }
}
