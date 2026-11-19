package accounts.account_service.security;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration}")
    private long expiration;

    public String generateToken(Authentication authentication) {

        Instant now = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plusMillis(expiration))
                .claim(
                        "authorities",
                        authentication.getAuthorities()
                                .stream()
                                .map(auth -> auth.getAuthority())
                                .toList()
                ).build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();
        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, jwtClaimsSet)).getTokenValue();
    }

}
