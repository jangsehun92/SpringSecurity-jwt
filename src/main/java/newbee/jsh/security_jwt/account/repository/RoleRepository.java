package newbee.jsh.security_jwt.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import newbee.jsh.security_jwt.account.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{

    Optional<Role> findByValue(final String value);
    
}
