package accounts.account_service.dto;

import lombok.Builder;

@Builder
public record AccountResponseDto(
        Long accountId,
        String login,
        String password
) {
}
