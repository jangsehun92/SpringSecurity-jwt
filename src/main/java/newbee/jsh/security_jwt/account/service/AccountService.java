package newbee.jsh.security_jwt.account.service;

import newbee.jsh.security_jwt.account.dto.request.RequestAccountCreateDto;
import newbee.jsh.security_jwt.account.dto.request.RequestAccountLoginDto;
import newbee.jsh.security_jwt.account.dto.response.ResponseTokensDto;
import newbee.jsh.security_jwt.account.entity.Account;

public interface AccountService {

    //계정 생성
    public void createAccount(final RequestAccountCreateDto dto);
    //로그인 (return tokens)
    public ResponseTokensDto login(final RequestAccountLoginDto dto);
    //계정 찾기 (findByEmail)
    public Account getAccount(final String email);
    
}
