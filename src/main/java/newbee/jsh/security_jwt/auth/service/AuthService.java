package newbee.jsh.security_jwt.auth.service;

import javax.servlet.http.HttpServletRequest;

import newbee.jsh.security_jwt.auth.dto.request.RequestJwtReissuanceDto;
import newbee.jsh.security_jwt.auth.dto.request.RequestLoginDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseAccessTokenDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseTokensDto;

public interface AuthService {

    //로그인
    public ResponseTokensDto login(final RequestLoginDto dto);

    //로그아웃
    public void logout(HttpServletRequest request);
    
    //accessToken 재발급
    public ResponseAccessTokenDto jwtReissuance(final RequestJwtReissuanceDto dto);
    
    
}
