package accounts.account_service.dto;

import accounts.account_service.AccountEntity;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
            componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    @Mapping(target = "login", source = "AccountRequestDto.login")
    @Mapping(target = "password", source = "AccountRequestDto.password")
    AccountEntity toEntity (AccountRequestDto requestDto);

    @Mapping(target = "login", source = "AccountEntity.login")
    @Mapping(target = "password", source = "AccountEntity.password")
    AccountResponseDto toAccountDto(AccountEntity account);
}
