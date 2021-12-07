package newbee.jsh.security_jwt.account.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import newbee.jsh.security_jwt.account.dto.request.RequestAccountCreateDto;
import newbee.jsh.security_jwt.account.dto.request.RequestAccountLoginDto;
import newbee.jsh.security_jwt.account.dto.response.ResponseTokensDto;
import newbee.jsh.security_jwt.account.service.AccountService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;

    //계정 생성
    @PostMapping(value="/account")
    public ResponseEntity<HttpStatus> createAccount(@RequestBody RequestAccountCreateDto dto) {
        log.info("POST /api/account {}", dto.toString());
        accountService.createAccount(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //로그인
    @PostMapping(value="/account/login")
    public ResponseEntity<ResponseTokensDto> login(@RequestBody RequestAccountLoginDto dto) {
        log.info("POST /api/account/login {}", dto.toString());
        return new ResponseEntity<>(accountService.login(dto), HttpStatus.OK);
    }
    
}
