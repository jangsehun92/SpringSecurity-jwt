package newbee.jsh.security_jwt.account.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import net.minidev.json.JSONObject;
import newbee.jsh.security_jwt.account.dto.request.RequestAccountCreateDto;
import newbee.jsh.security_jwt.account.service.AccountService;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    private MockMvc mvc;

    private ObjectMapper objectMapper;

    private JSONObject  jsonObject;

    @BeforeEach
    void before(){
        this.mvc = MockMvcBuilders
                        .standaloneSetup(accountController)
                        .addFilters(new CharacterEncodingFilter("UTF-8", true))
                        .alwaysDo(MockMvcResultHandlers.print()).build();
        
        this.objectMapper = new ObjectMapper();
        this.jsonObject = new JSONObject();
    }

    @Test
    @DisplayName("계정 생성 요청 성공")
    void createAccountFailByAlreadyEmail() throws Exception{
        //given
        final String email = "test@email.com";
        final String password = "password";
        final String roleValue = "USER";

        jsonObject.put("email", email);
        jsonObject.put("password", password);
        jsonObject.put("roleValue", roleValue);

        final RequestAccountCreateDto dto = objectMapper.readValue(jsonObject.toString(), RequestAccountCreateDto.class);

        //when
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                                                                .post("/api/account")
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(objectMapper.writeValueAsString(dto)))
                                                                .andReturn().getResponse();                                           
        //then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    
}
