package newbee.jsh.security_jwt.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
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
import newbee.jsh.security_jwt.auth.exception.AuthNotFoundException;
import newbee.jsh.security_jwt.auth.exception.JwtNotFoundException;
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
    private final AuthRepository authRepository; //accessToken 재발급 관련 정보 repository
    private final AuthBlackListRepository authBlackListRepository; // accessToken blackList repository

    @Transactional
    @Override
    public ResponseTokensDto login(final RequestLoginDto dto){
        final Account account = getAccount(dto.getEmail());
        checkPassword(dto.getPassword(), account);
        
        final String refreshTokenValue = RandomStringUtil.getRandomString(64);
        
        final String accessToken = jwtProvider.createAccessToken(account.getEmail());
        final String refreshToken = jwtProvider.createRefreshToken(refreshTokenValue);

        saveAuth(account.getEmail(), refreshTokenValue);

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

    private void saveAuth(final String email, final String refreshTokenValue){
        authRepository.save(Auth.builder()
                                .email(email)
                                .refreshTokenValue(refreshTokenValue).build());
    }

    @Transactional
    @Override
    public void logout(final HttpServletRequest request){
        final String accessToken = jwtProvider.resolveJwt(request);

        if(accessToken == null){ 
            throw new JwtNotFoundException(); 
        }

        final String email = jwtProvider.getSubject(accessToken);

        authRepository.delete(getAuth(email));
        authBlackListRepository.save(AuthBlackList.builder()
                                                    .email(email)
                                                    .accessToken(accessToken).build());
    }

    private Auth getAuth(final String email){
        return authRepository.findById(email).orElseThrow(AuthNotFoundException::new);
    }

    @Transactional
    @Override
    public ResponseAccessTokenDto jwtReissuance(RequestJwtReissuanceDto dto) {
        //1. tokens null check

        //2. accessToken 에서 email 추출 (만료 체크 안함)

        //3. accessToken 만료 시 해당 Exception에서 email(subject) 추출

        //4. 해당 email로 auth 정보 조회
        
        //5. 비교 후 accessToken 재생성 후 return
        return null;
    }
    
}
