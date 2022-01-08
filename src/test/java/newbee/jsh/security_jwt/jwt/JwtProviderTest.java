package newbee.jsh.security_jwt.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.SignatureException;
import newbee.jsh.security_jwt.auth.entity.AuthBlackList;
import newbee.jsh.security_jwt.auth.repository.AuthBlackListRepository;
import newbee.jsh.security_jwt.config.CustomUserDetails;
import newbee.jsh.security_jwt.config.CustomUserDetailsService;
import newbee.jsh.security_jwt.config.jwt.JwtAuthConstatns;
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
        final String email = "test@email.com";

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
        final String email = "test@email.com";
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
        final String email = "test@email.com";
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
        final String email = "test@email.com";
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
        final String email = "test@email.com";
        final String accessToken = jwtProvider.createAccessToken(email);

        //when
        boolean result = jwtProvider.dateValid(accessToken);

        //then
        assertEquals(true, result);
    }
    
    @Test
    @DisplayName("token blackList true")
    void isBlackListTrue() throws Exception{
        //given
        final String email = "test@email.com";
        final String accessToken = jwtProvider.createAccessToken(email);

        given(authBlackListRepository.findByEmailAndAccessToken(email, accessToken)).willReturn(AuthBlackList.builder().build());

        //when
        boolean result = jwtProvider.isBlackList(accessToken);

        //then
        assertEquals(true, result);
    }

    @Test
    @DisplayName("token blackList false")
    void isBlackListFalse() throws Exception{
        //given
        final String email = "test@email.com";
        final String accessToken = jwtProvider.createAccessToken(email);

        given(authBlackListRepository.findByEmailAndAccessToken(email, accessToken)).willReturn(null);

        //when
        final boolean result = jwtProvider.isBlackList(accessToken);

        //then
        assertEquals(false, result);
    }

    @Test
    @DisplayName("refreshTokenValue valid true")
    void refreshTokenValueValidTrue() throws Exception{
        //given
        final String refreshTokenValue = RandomStringUtil.getRandomString(32);

        final String refreshToken = jwtProvider.createRefreshToken(refreshTokenValue);

        //when
        final boolean result = jwtProvider.refreshTokenValueValid(refreshToken, refreshTokenValue);

        //then
        assertEquals(true, result);
    }

    @Test
    @DisplayName("refreshTokenValue valid false")
    void refreshTokenValueValidFalse() throws Exception{
        //given
        final String refreshTokenValue = RandomStringUtil.getRandomString(32);

        final String refreshToken = jwtProvider.createRefreshToken(refreshTokenValue);

        //when
        final boolean result = jwtProvider.refreshTokenValueValid(refreshToken, "TEST");

        //then
        assertEquals(false, result);
    }

    @Test
    @DisplayName("HttpServletReqeust내에 Authorization가 없어서 resolveJwt 결과 null")
    void resolveJwtNullByAuthorization() throws Exception{
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        final String result = jwtProvider.resolveJwt(request);

        //then
        assertEquals(null, result);
    }

    @Test
    @DisplayName("HttpServletReqeust내에 Authorization의 값이 'BEARER '로 시작하지 않아서 resolveJwt 결과 null")
    void resolveJwtNullByBEARER() throws Exception{
        //given
        final String email = "test@email.com";
        final String accessToken = jwtProvider.createAccessToken(email);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtAuthConstatns.AUTH_HEADER, accessToken);
        //when
        final String result = jwtProvider.resolveJwt(request);

        //then
        assertEquals(null, result);
    }

    @Test
    @DisplayName("HttpServletReqeust내에 jwt 얻기")
    void resolveJwt() throws Exception{
        //given
        final String email = "test@email.com";
        final String accessToken = jwtProvider.createAccessToken(email);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtAuthConstatns.AUTH_HEADER, JwtAuthConstatns.TOKEN_TYPE + accessToken);

        //when
        final String result = jwtProvider.resolveJwt(request);

        //then
        assertEquals(false, result.isEmpty());
    }

    @Test
    @DisplayName("UsernamePasswordAuthenticationToken 생성 성공")
    void getAuthenticationFailByAccountNotFound() throws Exception{
        //given
        final String email = "test@email.com";
        final String accessToken = jwtProvider.createAccessToken(email);

        given(customUserDetailsService.loadUserByUsername(anyString())).willReturn(CustomUserDetails.builder()
                                                                                                    .email(email)
                                                                                                    .authorities(Arrays.asList("USER").stream()
                                                                                                                                        .map(SimpleGrantedAuthority::new)
                                                                                                                                        .collect(Collectors.toList()))
                                                                                                    .build());
        //when
        final Authentication authentication = jwtProvider.getAuthentication(accessToken);

        //then
        assertEquals(true, authentication.getPrincipal().equals(email));
    }

}