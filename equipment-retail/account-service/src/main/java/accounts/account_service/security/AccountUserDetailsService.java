package accounts.account_service.security;

import accounts.account_service.db.AccountRepository;
import accounts.account_service.domain.AccountEntity;
import accounts.account_service.dto.AccountMapper;
import accounts.account_service.dto.LoginRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountUserDetailsService implements UserDetailsService{

    private final AccountRepository accountRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountEntity account = accountRepository.findByLogin(username)
                .orElseThrow(() -> new EntityNotFoundException("Account not found by Login: "+ username));

        return User.builder()
                .username(account.getLogin())
                .password(account.getPasswordHash())
                .authorities("ROLE_USER")
                .build();
    }
}