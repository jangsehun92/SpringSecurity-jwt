package newbee.jsh.security_jwt.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import newbee.jsh.security_jwt.auth.dto.request.RequestJwtReissuanceDto;
import newbee.jsh.security_jwt.auth.dto.request.RequestLoginDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseAccessTokenDto;
import newbee.jsh.security_jwt.auth.dto.response.ResponseTokensDto;
import newbee.jsh.security_jwt.auth.service.AuthService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    //로그인
    @PostMapping(value="/auth/login")
    public ResponseEntity<ResponseTokensDto> login(@RequestBody RequestLoginDto dto) {
        log.info("POST /api/auth/login {}", dto.toString());
        return new ResponseEntity<>(authService.login(dto), HttpStatus.OK);
    }

    //accessToken 재발급
    @PostMapping(value="/auth/jwt/reissuance")
    public ResponseEntity<ResponseAccessTokenDto> postMethodName(@RequestBody RequestJwtReissuanceDto dto) {
        log.info("POST /api/auth/jwt/reissuance {}", dto.toString());
        return new ResponseEntity<>(authService.jwtReissuance(dto), HttpStatus.OK);
    }
    
    
}
