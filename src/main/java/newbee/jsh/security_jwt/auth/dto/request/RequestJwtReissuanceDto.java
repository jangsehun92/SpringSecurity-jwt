package newbee.jsh.security_jwt.auth.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class RequestJwtReissuanceDto {

    private String accessToken;
    private String refreshToken;
    
}
