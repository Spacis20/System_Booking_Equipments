package accounts.account_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountId", nullable = false, unique = true)
    private Long accountId;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "passwordHash", nullable = false)
    private String passwordHash;

}
