package newbee.jsh.security_jwt.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.SignatureException;
import newbee.jsh.security_jwt.auth.repository.AuthBlackListRepository;
import newbee.jsh.security_jwt.config.CustomUserDetailsService;
import newbee.jsh.security_jwt.config.jwt.JwtProvider;
import newbee.jsh.security_jwt.global.util.RandomStringUtil;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    private JwtProvider jwtProvider;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private AuthBlackListRepository authBlackListRepository;

    @BeforeEach
    void before() throws Exception{
        this.jwtProvider = new JwtProvider(customUserDetailsService, authBlackListRepository);
        Method method = JwtProvider.class.getDeclaredMethod("init");
        method.setAccessible(true);
        method.invoke(jwtProvider);
    }

    @Test
    @DisplayName("accessToken 생성")
    void createAccessToken() throws Exception{
        //given
        final String email = "test@.com";

        //when
        final String accessToken = jwtProvider.createAccessToken(email);

        //then
        assertEquals(false, accessToken.isEmpty());
    }

    @Test
    @DisplayName("refreshToken 생성")
    void createRefreshToken() throws Exception{
        //given
        final String refreshTokenValue = RandomStringUtil.getRandomString(32);

        //when
        final String refreshToken = jwtProvider.createRefreshToken(refreshTokenValue);

        //then
        assertEquals(false, refreshToken.isEmpty());
    }

    @Test
    @DisplayName("signingKey가 달라 token 검증 실패")
    void getClaimsFailBySigningKey() throws Exception{
        //given
        final String email = "test@.com";
        final String accessToken = jwtProvider.createAccessToken(email);

        Method method = JwtProvider.class.getDeclaredMethod("init");
        method.setAccessible(true);
        method.invoke(jwtProvider);

        //when
        final Exception exception = assertThrows(Exception.class, ()->{
            jwtProvider.getClaims(accessToken);
        });

        //then
        assertEquals(exception.getClass(), SignatureException.class);
    }

    @Test
    @DisplayName("token 검증 성공 후 Jwt<Claims> 가져오기 성공")
    void getClaims() throws Exception{
        //given
        final String email = "test@.com";
        final String accessToken = jwtProvider.createAccessToken(email);

        //when
        Jws<Claims> claims = jwtProvider.getClaims(accessToken);

        //then
        assertEquals(email, claims.getBody().getSubject());
    }

    @Test
    @DisplayName("Subject 가져오기 성공")
    void getSubject() throws Exception{
        //given
        final String email = "test@.com";
        final String accessToken = jwtProvider.createAccessToken(email);

        //when
        final String subject = jwtProvider.getSubject(accessToken);

        //then
        assertEquals(subject, email);
    }

    @Test
    @DisplayName("dateValid 성공")
    void dateValid() throws Exception{
        //given
        final String email = "test@.com";
        final String accessToken = jwtProvider.createAccessToken(email);

        //when
        boolean result = jwtProvider.dateValid(accessToken);

        //then
        assertEquals(true, result);
    }






}
