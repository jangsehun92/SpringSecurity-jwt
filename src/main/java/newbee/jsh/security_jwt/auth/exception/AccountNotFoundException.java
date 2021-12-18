package newbee.jsh.security_jwt.auth.exception;

import newbee.jsh.security_jwt.global.error.exception.BusinessException;
import newbee.jsh.security_jwt.global.error.exception.ErrorCode;

public class AccountNotFoundException extends BusinessException {

    public AccountNotFoundException() {
        super(ErrorCode.ACCOUNT_NOT_FOUND);
    }
    
}
