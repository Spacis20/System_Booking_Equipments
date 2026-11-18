package accounts.account_service.dto;

import accounts.account_service.domain.AccountEntity;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
            componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

//    @Mapping(target = "login", source = "AccountRequestDto.login")
    @Mapping(target = "passwordHash", ignore = true)
    AccountEntity toEntity (AccountRequestDto requestDto);

//    @Mapping(target = "login", source = "AccountEntity.login")
//    @Mapping(target = "password", source = "AccountEntity.password")
    AccountResponseDto toAccountDto(AccountEntity account);


}
