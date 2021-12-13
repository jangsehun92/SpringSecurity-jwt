package newbee.jsh.security_jwt.account.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import newbee.jsh.security_jwt.account.dto.request.RequestAccountCreateDto;
import newbee.jsh.security_jwt.account.entity.Account;
import newbee.jsh.security_jwt.account.exception.EmailAlreadUsedException;
import newbee.jsh.security_jwt.account.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    //passwordEncoder

    @Override
    public void createAccount(final RequestAccountCreateDto dto) {
        checkAccount(dto.getEmail());
        //save()
                
    }

    private void checkAccount(final String email){
        accountRepository.findByEmail(email).ifPresent(Account -> {
            throw new EmailAlreadUsedException();
        });
    }

    @Override
    public Account getAccount(String email) {
        return accountRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException(email));
    }
    
}
