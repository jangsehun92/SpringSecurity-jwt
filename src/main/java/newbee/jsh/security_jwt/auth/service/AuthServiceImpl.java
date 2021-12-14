package newbee.jsh.security_jwt.auth.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import newbee.jsh.security_jwt.auth.dto.request.RequestJwtReissuanceDto;
import newbee.jsh.security_jwt.auth.dto.request.RequestLoginDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseAccessTokenDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseTokensDto;
import newbee.jsh.security_jwt.auth.repository.AuthRepository;
import newbee.jsh.security_jwt.config.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final AuthRepository authRepository;

    @Override
    public ResponseTokensDto login(final RequestLoginDto dto) {
        
        return null;
    }

    @Override
    public ResponseAccessTokenDto jwtReissuance(RequestJwtReissuanceDto dto) {
        
        return null;
    }
    
}