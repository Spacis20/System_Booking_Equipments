package accounts.account_service;

import accounts.account_service.domain.AuthProcessor;
import accounts.account_service.dto.LoginRequestDto;
import accounts.account_service.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt
            ){
        return Map.of(
                "userName", jwt.getSubject(),
                "authorities", jwt.getClaims(),
                "issuedAt", jwt.getIssuedAt(),
                "expiresAt", jwt.getExpiresAt()
        );
    }


}
