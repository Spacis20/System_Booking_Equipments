package accounts.account_service;

import accounts.account_service.domain.AuthProcessor;
import accounts.account_service.dto.LoginRequestDto;
import accounts.account_service.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthProcessor authProcessor;

    @PostMapping("/login")
    public LoginResponseDto login(
            @RequestBody LoginRequestDto requestDto){
        log.info("Entering by login: {}", requestDto.login());
        return authProcessor.login(requestDto);
    }


}
