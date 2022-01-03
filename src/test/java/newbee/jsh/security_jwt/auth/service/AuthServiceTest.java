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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import net.minidev.json.JSONObject;
import newbee.jsh.security_jwt.account.entity.Account;
import newbee.jsh.security_jwt.account.repository.AccountRepository;
import newbee.jsh.security_jwt.auth.dto.request.RequestJwtReissuanceDto;
import newbee.jsh.security_jwt.auth.dto.request.RequestLoginDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseAccessTokenDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseTokensDto;
import newbee.jsh.security_jwt.auth.entity.Auth;
import newbee.jsh.security_jwt.auth.entity.AuthBlackList;
import newbee.jsh.security_jwt.auth.exception.AccountNotFoundException;
import newbee.jsh.security_jwt.auth.exception.AccountPasswordNotMatchException;
import newbee.jsh.security_jwt.auth.exception.AuthNotFoundException;
import newbee.jsh.security_jwt.auth.exception.JwtNotFoundException;
import newbee.jsh.security_jwt.auth.exception.JwtVerificationException;
import newbee.jsh.security_jwt.auth.repository.AuthBlackListRepository;
import newbee.jsh.security_jwt.auth.repository.AuthRepository;
import newbee.jsh.security_jwt.config.CustomUserDetailsService;
import newbee.jsh.security_jwt.config.jwt.JwtAuthConstatns;
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

    @Test
    @DisplayName("accessToken을 찾을 수 없어서 로그아웃 실패")
    void logoutFailByJwtNotFound() throws Exception{
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader(JwtAuthConstatns.AUTH_HEADER, "");

        //when
        final Exception exception = assertThrows(Exception.class, ()->{
            authService.logout(request);
        });

        //then
        assertEquals(exception.getClass(), JwtNotFoundException.class);
    }

    @Test
    @DisplayName("Auth를 찾을 수 없어서 로그아웃 실패") 
    //로그인에 성공하면 AccessToken을 재발급 받을 수 있는 RefreshValue를 Auth로 저장한다.
    void logoutFailByAuthNotFound() throws Exception{
        //given
        final String accessToken = jwtProvider.createAccessToken("test@email.com");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader(JwtAuthConstatns.AUTH_HEADER, "BEARER " + accessToken);

        given(authRepository.findById(anyString())).willReturn(Optional.empty());

        //when
        final Exception exception = assertThrows(Exception.class, ()->{
            authService.logout(request);
        });

        //then
        assertEquals(exception.getClass(), AuthNotFoundException.class);
    }

    @Test
    @DisplayName("로그아웃 성공") 
    void logout() throws Exception{
        //given
        final String accessToken = jwtProvider.createAccessToken("test@email.com");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader(JwtAuthConstatns.AUTH_HEADER, "BEARER " + accessToken);

        given(authRepository.findById(anyString())).willReturn(Optional.of(Auth.builder().build()));

        //when
        authService.logout(request);

        //then
        then(authRepository).should(times(1)).delete(any(Auth.class));
        then(authBlackListRepository).should(times(1)).save(any(AuthBlackList.class));
    }

    @Test
    @DisplayName("token null로 인한 accessToken 재발급 실패")
    void jwtReissuanceFailByTokensNull() throws Exception{
        //given
        final String accessToken = "";
        final String refreshToken = "";

        jsonObject.put("accessToken", accessToken);
        jsonObject.put("refreshToken", refreshToken);

        final RequestJwtReissuanceDto dto = objectMapper.readValue(jsonObject.toString(), RequestJwtReissuanceDto.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader(JwtAuthConstatns.AUTH_HEADER, "BEARER " + accessToken);

        //when
        final Exception exception = assertThrows(Exception.class, ()->{
            authService.jwtReissuance(dto);
        });

        //then
        assertEquals(exception.getClass(), JwtNotFoundException.class);
    }

    @Test
    @DisplayName("회원을 찾을 수 없어 accessToken 재발급 실패")
    void jwtReissuanceFailByAccountNotFound() throws Exception{
        //given
        final String email = "test@email.com";
        final String accessToken = jwtProvider.createAccessToken(email);
        final String refreshToken = jwtProvider.createRefreshToken("value");

        jsonObject.put("accessToken", accessToken);
        jsonObject.put("refreshToken", refreshToken);

        final RequestJwtReissuanceDto dto = objectMapper.readValue(jsonObject.toString(), RequestJwtReissuanceDto.class);

        given(accountRepository.findByEmail(email)).willReturn(Optional.empty());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader(JwtAuthConstatns.AUTH_HEADER, "BEARER " + accessToken);

        //when
        final Exception exception = assertThrows(Exception.class, ()->{
            authService.jwtReissuance(dto);
        });

        //then
        assertEquals(exception.getClass(), AccountNotFoundException.class);
        then(accountRepository).should(times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("refreshToken value valid에 실패하여 accessToken 재발급 실패")
    void jwtReissuanceFailByAuthNotFound() throws Exception{
        //given
        final String email = "test@email.com";
        final String accessToken = jwtProvider.createAccessToken(email);
        final String refreshToken = jwtProvider.createRefreshToken("value");

        jsonObject.put("accessToken", accessToken);
        jsonObject.put("refreshToken", refreshToken);

        final RequestJwtReissuanceDto dto = objectMapper.readValue(jsonObject.toString(), RequestJwtReissuanceDto.class);

        given(accountRepository.findByEmail(email)).willReturn(Optional.of(Account.builder().build()));
        given(authRepository.findById(email)).willReturn(Optional.of(Auth.builder()
                                                                        .email(email)
                                                                        .refreshTokenValue("test")
                                                                        .build()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader(JwtAuthConstatns.AUTH_HEADER, "BEARER " + accessToken);

        //when
        final Exception exception = assertThrows(Exception.class, ()->{
            authService.jwtReissuance(dto);
        });

        //then
        assertEquals(exception.getClass(), JwtVerificationException.class);
        then(accountRepository).should(times(1)).findByEmail(anyString());
        then(authRepository).should(times(1)).findById(anyString());
    }

    @Test
    @DisplayName("accessToken 재발급 성공")
    void jwtReissuance() throws Exception{
        //given
        final String email = "test@email.com";
        final String accessToken = jwtProvider.createAccessToken(email);
        final String refreshToken = jwtProvider.createRefreshToken("value");

        jsonObject.put("accessToken", accessToken);
        jsonObject.put("refreshToken", refreshToken);

        final RequestJwtReissuanceDto dto = objectMapper.readValue(jsonObject.toString(), RequestJwtReissuanceDto.class);

        given(accountRepository.findByEmail(email)).willReturn(Optional.of(Account.builder().build()));
        given(authRepository.findById(email)).willReturn(Optional.of(Auth.builder()
                                                                        .email(email)
                                                                        .refreshTokenValue("value")
                                                                        .build()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader(JwtAuthConstatns.AUTH_HEADER, "BEARER " + accessToken);

        //when
        final ResponseAccessTokenDto responseAccessTokenDto = authService.jwtReissuance(dto);

        //then
        assertEquals(false, responseAccessTokenDto.getAccessToken().isEmpty());
        then(accountRepository).should(times(1)).findByEmail(anyString());
        then(authRepository).should(times(1)).findById(anyString());
    }
    
}
