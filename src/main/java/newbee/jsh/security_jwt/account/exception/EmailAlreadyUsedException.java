package newbee.jsh.security_jwt.account.exception;

import newbee.jsh.security_jwt.global.error.exception.BusinessException;
import newbee.jsh.security_jwt.global.error.exception.ErrorCode;

public class EmailAlreadyUsedException extends BusinessException {

    public EmailAlreadyUsedException() {
        super(ErrorCode.EMAIL_ALREADY_USED);
    }
    
}
