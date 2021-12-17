package newbee.jsh.security_jwt.config;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import newbee.jsh.security_jwt.account.entity.Account;
import newbee.jsh.security_jwt.account.entity.Role;
import newbee.jsh.security_jwt.account.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Account account = accountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return CustomUserDetails.builder()
                                .email(account.getEmail())
                                .password(account.getPassword())
                                .authorities(account.getRoles().stream().map(Role::getValue)
                                                                        .map(SimpleGrantedAuthority::new)
                                                                        .collect(Collectors.toList()))
                                .build();
    }
    
}
