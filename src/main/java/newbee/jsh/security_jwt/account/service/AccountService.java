package newbee.jsh.security_jwt.account.service;

import newbee.jsh.security_jwt.account.dto.request.RequestAccountCreateDto;
import newbee.jsh.security_jwt.account.dto.response.ResponseAccountDto;

public interface AccountService {

    //계정 생성
    public void createAccount(final RequestAccountCreateDto dto);
    //계정 찾기 (findByEmail)
    public ResponseAccountDto getAccount(final String email);
    
}
