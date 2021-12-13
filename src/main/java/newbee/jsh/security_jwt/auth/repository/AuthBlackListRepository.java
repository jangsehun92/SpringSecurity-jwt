package newbee.jsh.security_jwt.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import newbee.jsh.security_jwt.auth.entity.AuthBlackList;

public interface AuthBlackListRepository extends JpaRepository<AuthBlackList, String> {

    AuthBlackList findByEmailAndAccessToken(final String email, final String accessToken);
    
}
