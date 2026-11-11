package accounts.account_service.dto;


public record AccountRequestDto(
        String login,
        String password
) {
}
