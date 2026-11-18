package accounts.account_service.dto;

import lombok.Builder;

@Builder
public record LoginResponseDto(
        String accessToken,
        String tokenType
) {
}
