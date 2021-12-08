package newbee.jsh.security_jwt.config.jwt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtAuthConstatns {

    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_TYPE = "BEARER ";
    public static final String TOKEN_AUDIENCE = "WEB";
    
}
