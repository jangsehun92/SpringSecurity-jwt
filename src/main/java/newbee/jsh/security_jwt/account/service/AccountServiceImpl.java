package newbee.jsh.security_jwt.account.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import newbee.jsh.security_jwt.account.dto.request.RequestAccountCreateDto;
import newbee.jsh.security_jwt.account.dto.response.ResponseAccountDto;
import newbee.jsh.security_jwt.account.entity.Account;
import newbee.jsh.security_jwt.account.entity.Role;
import newbee.jsh.security_jwt.account.exception.EmailAlreadyUsedException;
import newbee.jsh.security_jwt.account.repository.AccountRepository;
import newbee.jsh.security_jwt.account.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void createAccount(final RequestAccountCreateDto dto) {
        checkAccount(dto.getEmail());
        accountRepository.save(Account.builder()
                                        .email(dto.getEmail())
                                        .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                                        .roles(Collections.singleton(getRole(dto.getRoleValue()))).build());
    }

    private void checkAccount(final String email){
        accountRepository.findByEmail(email).ifPresent(account -> {
            throw new EmailAlreadyUsedException();
        });
    }

    private Role getRole(final String roleValue){
        return roleRepository.findByValue(roleValue).orElseGet(() -> roleRepository.save(Role.builder().value(roleValue).build()));
    }

    @Override
    public ResponseAccountDto getAccount(String email) {
        return accountRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException(email)).toResponseAccountDto();
    }
    
}
