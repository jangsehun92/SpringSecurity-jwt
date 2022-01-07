package newbee.jsh.security_jwt.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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



}
