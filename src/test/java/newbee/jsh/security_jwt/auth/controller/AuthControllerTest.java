package newbee.jsh.security_jwt.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import net.minidev.json.JSONObject;
import newbee.jsh.security_jwt.auth.dto.request.RequestLoginDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseTokensDto;
import newbee.jsh.security_jwt.auth.repository.AuthBlackListRepository;
import newbee.jsh.security_jwt.auth.service.AuthService;
import newbee.jsh.security_jwt.config.CustomUserDetailsService;
import newbee.jsh.security_jwt.config.jwt.JwtProvider;
import newbee.jsh.security_jwt.global.util.RandomStringUtil;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @SpyBean
    private JwtProvider jwtProvider;

    @MockBean
    private AuthService authService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private AuthBlackListRepository authBlackListRepository;

    private JSONObject jsonObject;
    private ObjectMapper objectMapper;

    @BeforeEach
    void before(){
        this.jsonObject = new JSONObject();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("로그인 요청 성공")
    void test() throws Exception{
        //given
        final String email = "email@test.com";
        final String password = "password";

        jsonObject.put("email", email);
        jsonObject.put("password", password);

        final RequestLoginDto dto = objectMapper.readValue(jsonObject.toString(), RequestLoginDto.class);

        // String accessToken = jwtProvider.createAccessToken("email");
        final ResponseTokensDto givenResponseTokensDto = ResponseTokensDto.builder()
                                                                    .accessToken(jwtProvider.createAccessToken("email"))
                                                                    .refreshToken(jwtProvider.createRefreshToken(RandomStringUtil.getRandomString(32)))
                                                                    .build();
        given(authService.login(any(RequestLoginDto.class))).willReturn(givenResponseTokensDto);

        //when
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                                                            .post("/api/auth/login")
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .content(objectMapper.writeValueAsString(dto)))
                                                            .andDo(MockMvcResultHandlers.print()).andReturn().getResponse();
        
        //then
        assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

    
}
