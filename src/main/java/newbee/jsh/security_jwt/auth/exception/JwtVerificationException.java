package newbee.jsh.security_jwt.auth.exception;

import newbee.jsh.security_jwt.global.error.exception.BusinessException;
import newbee.jsh.security_jwt.global.error.exception.ErrorCode;

public class JwtVerificationException extends BusinessException {

    public JwtVerificationException() {
        super(ErrorCode.JWT_VERIFICATION_FAILD);
    }
    
}
