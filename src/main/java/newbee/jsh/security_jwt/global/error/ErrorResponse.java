package newbee.jsh.security_jwt.global.error;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import lombok.ToString;
import newbee.jsh.security_jwt.global.error.exception.ErrorCode;

@ToString
public class ErrorResponse {

    private String message;
	private int status;
	private String code;
	private List<FieldError> errors;
	
	private ErrorResponse(final ErrorCode errorCode) {
		this.message = errorCode.getMessage();
		this.status = errorCode.getStatus();
		this.code = errorCode.getCode();
		this.errors = new ArrayList<>();
	}
	
	private ErrorResponse(final ErrorCode errorCode, final List<FieldError> fieldErrors) {
		this.message = errorCode.getMessage();
		this.status = errorCode.getStatus();
		this.code = errorCode.getCode();
		this.errors = fieldErrors;
	}

	public static ErrorResponse of(final ErrorCode errorCode){
		return new ErrorResponse(errorCode);
	}

	public static ErrorResponse of(final ErrorCode errorCode, final BindingResult bingingResult){
		return new ErrorResponse(errorCode, convertBingingResult(bingingResult));
	}

	public static List<FieldError> convertBingingResult(final BindingResult bindingResult){
		return bindingResult.getFieldErrors()
							.stream().map(error -> new FieldError(	error.getField(), 
																	error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
																	error.getDefaultMessage()))
							.collect(Collectors.toList());
	}
	
	public String getMessage() {
		return message;
	}


	public int getStatus() {
		return status;
	}


	public String getCode() {
		return code;
	}
	
	public List<FieldError> getErrors(){
		return errors;
	}
    
}
