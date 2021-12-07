package newbee.jsh.security_jwt.account.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class RequestAccountCreateDto {

    private String email;
    private String password;
    
}
