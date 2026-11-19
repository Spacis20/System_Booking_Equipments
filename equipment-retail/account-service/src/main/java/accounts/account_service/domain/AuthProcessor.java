package accounts.account_service.domain;

import accounts.account_service.dto.LoginRequestDto;
import accounts.account_service.dto.LoginResponseDto;
import accounts.account_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthProcessor {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponseDto login(LoginRequestDto login){

        var authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                login.login(),
                                login.password()
                        ));

        var token = jwtService.generateToken(authentication);
        return LoginResponseDto.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }

}
