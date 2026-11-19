package accounts.account_service.security;

import accounts.account_service.db.AccountRepository;
import accounts.account_service.domain.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService{

    private final AccountRepository accountRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountEntity account = accountRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found by Login: "+ username));

        return User.builder()
                .username(account.getLogin())
                .password(account.getPasswordHash())
                .authorities("ROLE_USER")
                .build();
    }
}