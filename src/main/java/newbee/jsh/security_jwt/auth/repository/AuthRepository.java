package newbee.jsh.security_jwt.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import newbee.jsh.security_jwt.auth.entity.Auth;

public interface AuthRepository extends JpaRepository<Auth, String> {
    
}
