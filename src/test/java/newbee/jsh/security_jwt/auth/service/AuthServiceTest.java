package newbee.jsh.security_jwt.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.lang.reflect.Method;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import net.minidev.json.JSONObject;
import newbee.jsh.security_jwt.account.entity.Account;
import newbee.jsh.security_jwt.account.repository.AccountRepository;
import newbee.jsh.security_jwt.auth.dto.request.RequestLoginDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseTokensDto;
import newbee.jsh.security_jwt.auth.entity.Auth;
import newbee.jsh.security_jwt.auth.exception.AccountNotFoundException;
import newbee.jsh.security_jwt.auth.exception.AccountPasswordNotMatchException;
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

    private JSONObject jsonObject;
    private ObjectMapper objectMapper;

    @BeforeEach
    void before() throws Exception{
        this.jwtProvider = new JwtProvider(customUserDetailsService, authBlackListRepository);
        /**
         * reflect private method 호출
         */
        Method method = JwtProvider.class.getDeclaredMethod("init");
        method.setAccessible(true);
        method.invoke(jwtProvider);

        /**
         * reflect private filed에 직접 접근하여 설정
         */
        // Field filed = JwtProvider.class.getDeclaredField("key");
        // filed.setAccessible(true);
        // filed.set(jwtProvider, Keys.secretKeyFor(SignatureAlgorithm.HS256));

        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.authService = new AuthServiceImpl(jwtProvider, bCryptPasswordEncoder, accountRepository, authRepository,authBlackListRepository);
        this.jsonObject = new JSONObject();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("계정을 찾을 수 없어서 로그인 실패")
    void loginFailByNotFoundAccount() throws Exception{
        //given
        final String email = "test@email.com";
        jsonObject.put("email", email);

        final RequestLoginDto dto = objectMapper.readValue(jsonObject.toString(), RequestLoginDto.class);

        given(accountRepository.findByEmail(anyString())).willReturn(Optional.empty());

        //when
        final Exception exception = assertThrows(Exception.class, ()->{
            authService.login(dto);
        });

        //then
        assertEquals(exception.getClass(), AccountNotFoundException.class);
    }

    @Test
    @DisplayName("비밀번호가 틀려서 로그인 실패")
    void loginFailByPasswordNotMatch() throws Exception{
        //given
        final String email = "test@email.com";
        final String password = "test";

        jsonObject.put("email",email);
        jsonObject.put("password",password);

        final RequestLoginDto dto = objectMapper.readValue(jsonObject.toString(), RequestLoginDto.class);

        final Account givenAccount = Account.builder()
                                            .password(bCryptPasswordEncoder.encode("password"))
                                            .build();

        given(accountRepository.findByEmail(anyString())).willReturn(Optional.of(givenAccount));

        //when
        final Exception exception = assertThrows(Exception.class, ()->{
            authService.login(dto);
        });

        //then
        assertEquals(exception.getClass(), AccountPasswordNotMatchException.class);
    }

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception{
        //given
        final String email = "test@email.com";
        final String password = "password";

        jsonObject.put("email",email);
        jsonObject.put("password",password);

        final RequestLoginDto dto = objectMapper.readValue(jsonObject.toString(), RequestLoginDto.class);

        final Account givenAccount = Account.builder()
                                            .password(bCryptPasswordEncoder.encode("password"))
                                            .build();
        
        given(accountRepository.findByEmail(anyString())).willReturn(Optional.of(givenAccount));

        //when
        final ResponseTokensDto responseTokensDto = authService.login(dto);

        //then
        then(authRepository).should(times(1)).save(any(Auth.class));
        System.out.println(responseTokensDto.toString());
    }
    
}
