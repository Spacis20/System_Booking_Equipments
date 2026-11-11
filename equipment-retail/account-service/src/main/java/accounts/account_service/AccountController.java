package accounts.account_service;


import accounts.account_service.dto.AccountMapper;
import accounts.account_service.dto.AccountRequestDto;
import accounts.account_service.dto.AccountResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountProcessor accountProcessor;
    private final AccountMapper accountMapper;

    @PostMapping
    public AccountResponseDto create(
            @RequestBody AccountRequestDto requestDto
            ){
        log.info("Creating account by login: {}", requestDto.login());
        return accountProcessor.create(requestDto);
    }

    @GetMapping()
    public AccountResponseDto getAccount(
            @RequestParam String login
    ){
        log.info("Get account by login: {}", login);
        var entity = accountProcessor.findByLogin(login);
        return accountMapper.toAccountDto(entity);
    }

    @GetMapping("/all")
    public Set<Object> getAllAccounts(){
        log.info("Get All accounts:");
        return accountProcessor.findAll();
    }


}
