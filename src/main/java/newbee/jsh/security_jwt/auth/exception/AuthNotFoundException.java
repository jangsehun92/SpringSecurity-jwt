package newbee.jsh.security_jwt.auth.exception;

import newbee.jsh.security_jwt.global.error.exception.BusinessException;
import newbee.jsh.security_jwt.global.error.exception.ErrorCode;

public class AuthNotFoundException extends BusinessException {

    public AuthNotFoundException() {
        super(ErrorCode.AUTH_NOT_FOUND);
    }
    
}
