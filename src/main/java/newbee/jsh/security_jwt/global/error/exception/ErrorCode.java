package newbee.jsh.security_jwt.global.error.exception;

public enum ErrorCode {

    //COMMON
    INVALID_INPUT_VALUE(400, "C001", " Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "C002", " METHOD NOT ALLOWED"),
    NOT_FOUND(404, "C003", " Not Found"),
    INTERNAL_SERVER_ERROR(500, "C004", " INTERNAL SERVER ERROR"),
    HANDLE_ACCESS_DENIED(403, "C005", " Access is denied"), //로그인은 했지만 권한 없음
	HANDEL_UNAUTHORIZED(401, "C006", " Unauthorized"), //로그인 필요(Security Context내에 설정되지 않음)

    //ACCOUNT
	EMAIL_ALREADY_USED(400, "A002", " 중복된 이메일 입니다."),
	PASSWORD_NOT_MATCH(400, "A003", " 이메일 또는 비밀번호가 다릅니다."),
	ACCOUNT_NOT_FOUND(400, "A004", " 계정을 찾을 수 없습니다.");

    private int status;
	private String code;
	private String message;
	
	ErrorCode(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}
    
}
