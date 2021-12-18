package newbee.jsh.security_jwt.auth.exception;

import newbee.jsh.security_jwt.global.error.exception.BusinessException;
import newbee.jsh.security_jwt.global.error.exception.ErrorCode;

public class AccountPasswordNotMatchException extends BusinessException {

    public AccountPasswordNotMatchException() {
        super(ErrorCode.PASSWORD_NOT_MATCH);
    }
    
}
