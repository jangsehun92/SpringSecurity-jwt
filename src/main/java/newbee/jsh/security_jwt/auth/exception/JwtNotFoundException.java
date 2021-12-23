package newbee.jsh.security_jwt.auth.exception;

import newbee.jsh.security_jwt.global.error.exception.BusinessException;
import newbee.jsh.security_jwt.global.error.exception.ErrorCode;

public class JwtNotFoundException extends BusinessException {

    public JwtNotFoundException() {
        super(ErrorCode.JWT_NOT_FOUND);
    }
    
}
