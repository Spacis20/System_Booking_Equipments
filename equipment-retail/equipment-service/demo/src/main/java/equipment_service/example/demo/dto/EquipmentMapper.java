package equipment_service.example.demo.dto;

import equipment_service.example.demo.db.equipment.EquipmentCategory;
import equipment_service.example.demo.db.equipment.EquipmentEntity;
import equipment_service.example.demo.db.equipment.EquipmentReservation;
import equipment_service.example.demo.dto.category.CategoryResponseDto;
import equipment_service.example.demo.dto.category.CreateCategoryRequestDto;
import equipment_service.example.demo.dto.equipment.CreateEquipmentRequestDto;
import equipment_service.example.demo.dto.equipment.EquipmentResponseDto;
import equipment_service.example.demo.dto.equipment.UpdateEquipmentRequestDto;
import equipment_service.example.demo.dto.reservation.CreateEquipmentReservationDto;
import equipment_service.example.demo.dto.reservation.ResponseReservationDto;
import org.mapstruct.*;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface EquipmentMapper {

    EquipmentEntity toEntity(CreateEquipmentRequestDto requestEntityDto);
    @Mapping(target = "categoryName", source = "equipmentCategory.name")
    EquipmentResponseDto toEquipmentDto(EquipmentEntity equipment);

    EquipmentReservation toEntityReservation(CreateEquipmentReservationDto reservationDto);
    @Mapping(target = "quantityDaysRental")
    ResponseReservationDto toReservationDto(EquipmentReservation reservation);

    EquipmentCategory toCategory(CreateCategoryRequestDto requestDto);
    CategoryResponseDto toCategoryDto(EquipmentCategory category);

    void updateEquipment(
            UpdateEquipmentRequestDto equipmentRequestDto,
            @MappingTarget EquipmentEntity equipment);



//    @AfterMapping
//    default void linkEquipmentEntities(@MappingTarget EquipmentCategory equipmentCategory){
//        equipmentCategory.getEquipments().forEach(equipmentEntity ->
//                equipmentEntity.setEquipmentCategory(equipmentCategory));
//    }
}
