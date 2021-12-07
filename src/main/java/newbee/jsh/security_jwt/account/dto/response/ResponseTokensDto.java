package newbee.jsh.security_jwt.account.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class ResponseTokensDto {

    private String accessToken;
    private String refreshToken;
    
}
