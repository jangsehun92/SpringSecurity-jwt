package newbee.jsh.security_jwt.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Collections;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import net.minidev.json.JSONObject;
import newbee.jsh.security_jwt.account.dto.request.RequestAccountCreateDto;
import newbee.jsh.security_jwt.account.dto.response.ResponseAccountDto;
import newbee.jsh.security_jwt.account.entity.Account;
import newbee.jsh.security_jwt.account.entity.Role;
import newbee.jsh.security_jwt.account.exception.EmailAlreadyUsedException;
import newbee.jsh.security_jwt.account.repository.AccountRepository;
import newbee.jsh.security_jwt.account.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private AccountService accountService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RoleRepository roleRepository;

    private ObjectMapper objectMapper;
    private JSONObject jsonObject;

    @BeforeEach
    void before(){
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.accountService = new AccountServiceImpl(accountRepository, roleRepository, bCryptPasswordEncoder);

        this.objectMapper = new ObjectMapper();
        this.jsonObject = new JSONObject();
    }

    @Test
    @DisplayName("중복된 Email로 인한 계정 생성 실패")
    void createAccountFailByAlreadyEmailUsed() throws Exception{
        //given
        final String email = "test@email.com";

        jsonObject.put("email", email);

        final RequestAccountCreateDto dto = objectMapper.readValue(jsonObject.toString(), RequestAccountCreateDto.class);

        given(accountRepository.findByEmail(anyString())).willReturn(Optional.of(Account.builder().build()));

        //when
        final Exception exception = assertThrows(Exception.class, ()->{
            accountService.createAccount(dto);
        });

        //then
        then(accountRepository).should(times(1)).findByEmail(anyString());
        assertEquals(exception.getClass(), EmailAlreadyUsedException.class);
    }

    @Test
    @DisplayName("계정 생성 성공")
    void createAccount() throws Exception{
        //given
        final String email = "test@email.com";
        final String password = "password";
        final String roleValue = "USER";

        jsonObject.put("email", email);
        jsonObject.put("password", password);
        jsonObject.put("roleValue", roleValue);

        final RequestAccountCreateDto dto = objectMapper.readValue(jsonObject.toString(), RequestAccountCreateDto.class);

        given(accountRepository.findByEmail(anyString())).willReturn(Optional.empty());

        //when
        accountService.createAccount(dto);

        //then
        then(accountRepository).should(times(1)).findByEmail(anyString());
        then(roleRepository).should(times(1)).findByValue(anyString());
        then(roleRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("계정을 찾을 수 없어서 실패")
    void getAccountFailByNotFoundAccount() throws Exception{
        //given
        given(accountRepository.findByEmail(anyString())).willReturn(Optional.empty());

        //when
        final Exception exception = assertThrows(Exception.class, ()->{
            accountService.getAccount(anyString());
        });

        //then
        then(accountRepository).should(times(1)).findByEmail(anyString());
        assertEquals(exception.getClass(), UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("계정 가져오기 성공")
    void getAccount() throws Exception{
        //given
        final String email = "test@email.com";
        final String roleValue = "USER";

        final Role role = Role.builder()
                                .value(roleValue)
                                .build();
        
        final Account givenAccount = Account.builder()
                                            .email(email)
                                            .roles(Collections.singleton(role))
                                            .build();
                                                                            
        given(accountRepository.findByEmail(anyString())).willReturn(Optional.of(givenAccount));

        //when
        final ResponseAccountDto responseAccountDto = accountService.getAccount(anyString());

        //then
        then(accountRepository).should(times(1)).findByEmail(anyString());
        assertEquals(responseAccountDto.getEmail(), email);
        assertEquals(true, responseAccountDto.getRoles().contains(role));
    }
    
}
