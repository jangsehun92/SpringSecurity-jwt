package newbee.jsh.security_jwt.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.filter.CharacterEncodingFilter;

import net.minidev.json.JSONObject;
import newbee.jsh.security_jwt.account.service.AccountService;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

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

    

    
}
