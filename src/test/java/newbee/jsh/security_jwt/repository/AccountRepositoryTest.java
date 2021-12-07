package newbee.jsh.security_jwt.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import newbee.jsh.security_jwt.account.entity.Account;
import newbee.jsh.security_jwt.account.entity.Role;
import newbee.jsh.security_jwt.account.repository.AccountRepository;
import newbee.jsh.security_jwt.account.repository.RoleRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    RoleRepository roleRepository;

    @BeforeEach
    void before(){
        roleRepository.save(Role.builder()
                                .value("USER").build());
    }

    @Test
    @DisplayName("Save Account")
    void saveAccount() throws Exception{
        //given
        Account givenAccount = Account.builder()
                                    .email("test@email.com")
                                    .password("password")
                                    .roles(Collections.singleton(roleRepository.findByValue("USER").get())).build();
        
        //when
        Account account = accountRepository.save(givenAccount);


        //then
        assertNotNull(account.getId());
    }
    
}
