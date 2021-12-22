package newbee.jsh.security_jwt.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import newbee.jsh.security_jwt.account.entity.Account;
import newbee.jsh.security_jwt.account.repository.AccountRepository;
import newbee.jsh.security_jwt.auth.dto.request.RequestJwtReissuanceDto;
import newbee.jsh.security_jwt.auth.dto.request.RequestLoginDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseAccessTokenDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseTokensDto;
import newbee.jsh.security_jwt.auth.entity.Auth;
import newbee.jsh.security_jwt.auth.entity.AuthBlackList;
import newbee.jsh.security_jwt.auth.exception.AccountNotFoundException;
import newbee.jsh.security_jwt.auth.exception.AccountPasswordNotMatchException;
import newbee.jsh.security_jwt.auth.repository.AuthBlackListRepository;
import newbee.jsh.security_jwt.auth.repository.AuthRepository;
import newbee.jsh.security_jwt.config.jwt.JwtProvider;
import newbee.jsh.security_jwt.global.util.RandomStringUtil;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountRepository accountRepository;
    private final AuthRepository authRepository;
    private final AuthBlackListRepository authBlackListRepository;

    @Transactional
    @Override
    public ResponseTokensDto login(final RequestLoginDto dto){
        final Account account = getAccount(dto.getEmail());
        checkPassword(dto.getPassword(), account);
        
        final String refreshTokenValue = RandomStringUtil.getRandomString(64);
        
        final String accessToken = jwtProvider.createAccessToken(account.getEmail());
        final String refreshToken = jwtProvider.createRefreshToken(refreshTokenValue);

        saveRefreshTokenValue(account.getEmail(), refreshTokenValue);

        return ResponseTokensDto.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken).build();
    }

    private Account getAccount(final String email){
        return accountRepository.findByEmail(email).orElseThrow(AccountNotFoundException::new);
    }

    private void checkPassword(final String requestPassword, final Account account){
        if(!bCryptPasswordEncoder.matches(requestPassword, account.getPassword())){
            throw new AccountPasswordNotMatchException();
        }
    }

    private void saveRefreshTokenValue(final String email, final String refreshTokenValue){
        authRepository.save(Auth.builder()
                                .email(email)
                                .refreshTokenValue(refreshTokenValue).build());
    }

    @Transactional
    @Override
    public void logout(final HttpServletRequest request){
        final String accessToken = jwtProvider.resolveJwt(request);

        final String email = jwtProvider.getSubject(accessToken);

        authBlackListRepository.save(AuthBlackList.builder()
                                                    .email(email)
                                                    .accessToken(accessToken).build());
    }

    @Transactional
    @Override
    public ResponseAccessTokenDto jwtReissuance(RequestJwtReissuanceDto dto) {
        return null;
    }
    
}
