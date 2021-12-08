package newbee.jsh.security_jwt.config.jwt;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import newbee.jsh.security_jwt.account.service.AccountService;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private SecretKey key;

    private final AccountService accountService;

    @PostConstruct
    private void init(){
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    //create AccessToken
    public String createAccessToken(final Long id, final String email, final Collection<? extends GrantedAuthority> authorities){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("id", id);
        claims.put("roles", authorities);
        Date regDate = new Date();
        Date expirationDate = new Date(regDate.getTime() + Duration.ofMinutes(30).toMillis());

        return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(regDate)
                    .setAudience(JwtAuthConstatns.TOKEN_AUDIENCE)
                    .setExpiration(expirationDate)
                    .signWith(key)
                    .compact();
    }

    //create RefreshToken 
    public String createRefreshToken(final String value){
        Claims claims = Jwts.claims().setSubject(value);
        Date regDate = new Date();
        Date expirationDate = new Date(regDate.getTime() + Duration.ofDays(1).toMillis());

        return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(regDate)
                    .setAudience(JwtAuthConstatns.TOKEN_AUDIENCE)
                    .setExpiration(expirationDate)
                    .signWith(key)
                    .compact();
    }

    //tokenDateValid

    //checkBlackListToken

    //get request Header Jwt

    //get Claims

    //create usernamePasswordAuthenticationToken

    //get Subject to Claims
    
}
