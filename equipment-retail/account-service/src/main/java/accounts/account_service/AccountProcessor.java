package accounts.account_service;

import accounts.account_service.dto.AccountMapper;
import accounts.account_service.dto.AccountRequestDto;
import accounts.account_service.dto.AccountResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class AccountProcessor {

    private final AccountMapper mapper;
    private final AccountRepository accountRepository;

    public AccountResponseDto create(AccountRequestDto requestDto) {
        var entity = mapper.toEntity(requestDto);
        var account = accountRepository.findByLogin(entity.getLogin());

        if (account.isPresent()){
            throw new EntityNotFoundException("That login already use: "+ requestDto.login());
        }
        var save = accountRepository.save(entity);
        return mapper.toAccountDto(save);
    }

    public AccountEntity findByLogin(String login){
        return accountRepository.findByLogin(login).orElseThrow(
                () -> new EntityNotFoundException("Account don't found: "+ login)
        );
    }

    public AccountResponseDto delete(AccountRequestDto requestDto){
        var account = findByLogin(requestDto.login());
        accountRepository.delete(account);
        log.info("Account login %s deleted", account.getLogin());
        return mapper.toAccountDto(account);
    }

    public Set<AccountResponseDto> findAll() {
        var list = new LinkedHashSet<>();
        accountRepository.findAll().stream()
                .map(mapper::toAccountDto)
                .forEach(list::add);
    }
}
