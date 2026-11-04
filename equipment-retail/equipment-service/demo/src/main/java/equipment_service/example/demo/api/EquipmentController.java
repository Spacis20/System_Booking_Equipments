package equipment_service.example.demo.api;


import equipment_service.example.demo.db.processors.EquipmentProcessor;
import equipment_service.example.demo.dto.EquipmentMapper;
import equipment_service.example.demo.dto.equipment.CreateEquipmentRequestDto;
import equipment_service.example.demo.dto.equipment.EquipmentResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {

    private final EquipmentMapper mapper;
    private final EquipmentProcessor processor;

    @PostMapping
    public EquipmentResponseDto create(
            @RequestBody CreateEquipmentRequestDto requestDto
            ){
        log.info("Equipment is creating");
        return processor.create(requestDto);
    }

    @GetMapping("/{id}")
    public EquipmentResponseDto getEquipment(
            @PathVariable Long id
    ){
        log.info("Equipment get by id: {}", id);
        return processor.getByid(id);
    }

    @GetMapping
    public Set<EquipmentResponseDto> getAllEquipments(){
        log.info("Get all equipments");
        return processor.getAll();
    }

    @PostMapping("/deleteAll")
    public void deleteAll(){
        log.info("Delete All dates");
        processor.deleteAll();
    }


}
