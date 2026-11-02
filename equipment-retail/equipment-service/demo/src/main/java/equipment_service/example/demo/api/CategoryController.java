package equipment_service.example.demo.api;

import equipment_service.example.demo.db.processors.CategoryProcessor;
import equipment_service.example.demo.dto.EquipmentMapper;
import equipment_service.example.demo.dto.category.CategoryResponseDto;
import equipment_service.example.demo.dto.category.CreateCategoryRequestDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/category")
@AllArgsConstructor
public class CategoryController {

    private final EquipmentMapper mapper;
    private final CategoryProcessor processor;

    @PostMapping
    public CategoryResponseDto create(
            @RequestBody CreateCategoryRequestDto requestDto
            ){
        log.info("Creating category...");
        return processor.create(requestDto);
    }

}
