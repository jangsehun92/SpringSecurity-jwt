package newbee.jsh.security_jwt.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import newbee.jsh.security_jwt.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{

    Optional<Account> findByEmail(final String email);
    
}
