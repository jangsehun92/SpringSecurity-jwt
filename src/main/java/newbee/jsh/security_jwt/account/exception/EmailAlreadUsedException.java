package newbee.jsh.security_jwt.account.exception;

import newbee.jsh.security_jwt.global.error.exception.BusinessException;
import newbee.jsh.security_jwt.global.error.exception.ErrorCode;

public class EmailAlreadUsedException extends BusinessException {

    public EmailAlreadUsedException() {
        super(ErrorCode.EMAIL_ALREADY_USED);
    }
    
}
