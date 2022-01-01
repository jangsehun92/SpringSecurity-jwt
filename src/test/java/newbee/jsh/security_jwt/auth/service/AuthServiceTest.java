package newbee.jsh.security_jwt.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import newbee.jsh.security_jwt.account.repository.AccountRepository;
import newbee.jsh.security_jwt.auth.repository.AuthBlackListRepository;
import newbee.jsh.security_jwt.auth.repository.AuthRepository;
import newbee.jsh.security_jwt.config.CustomUserDetailsService;
import newbee.jsh.security_jwt.config.jwt.JwtProvider;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private AuthService authService;
    private JwtProvider jwtProvider;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AuthRepository authRepository;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private AuthBlackListRepository authBlackListRepository;

    @BeforeEach
    void before(){
        this.jwtProvider = new JwtProvider(customUserDetailsService, authBlackListRepository);
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.authService = new AuthServiceImpl(jwtProvider, bCryptPasswordEncoder, accountRepository, authRepository,authBlackListRepository);
    }


    
}
