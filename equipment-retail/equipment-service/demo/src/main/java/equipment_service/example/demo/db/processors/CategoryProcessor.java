package equipment_service.example.demo.db.processors;

import equipment_service.example.demo.db.equipment.EquipmentCategoryRepository;
import equipment_service.example.demo.dto.EquipmentMapper;
import equipment_service.example.demo.dto.category.CategoryResponseDto;
import equipment_service.example.demo.dto.category.CreateCategoryRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryProcessor {

    private final EquipmentMapper mapper;
    private final EquipmentCategoryRepository repository;

    public CategoryResponseDto create(CreateCategoryRequestDto requestDto){
        var entity = mapper.toCategory(requestDto);
        var save = repository.save(entity);
        return mapper.toCategoryDto(save);
    }

}
