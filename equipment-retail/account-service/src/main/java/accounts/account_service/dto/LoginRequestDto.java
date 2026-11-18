package accounts.account_service.dto;


public record LoginRequestDto(
        String login,
        String password
) {
}
