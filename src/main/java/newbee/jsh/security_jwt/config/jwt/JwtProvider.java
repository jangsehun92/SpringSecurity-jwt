package newbee.jsh.security_jwt.config.jwt;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import newbee.jsh.security_jwt.account.entity.Account;
import newbee.jsh.security_jwt.account.entity.Role;
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

    //get Claims
    public Jws<Claims> getClaims(final String jwt){
        return Jwts.parserBuilder()
                    .requireAudience(JwtAuthConstatns.TOKEN_AUDIENCE)
                    .setSigningKey(key)
                    .build().parseClaimsJws(jwt);
    }

    //get Subject to Claims
    public String getSubject(final String jwt){
        return getClaims(jwt).getBody().getSubject();
    }

    //accessTokenDateValid
    public boolean dateValid(final String jwt){
        try{    
            Jws<Claims> claims = getClaims(jwt.trim());
            return !claims.getBody().getExpiration().before(new Date());
        }catch(Exception e){
            return false;
        }
    }

    //checkBlackListToken

    //refreshTokenValudValid
    public boolean refreshokenValueValid(final String refreshToken, final String value){
        if(getSubject(refreshToken).equals(value)){
            return true;
        }
        return false;
    }

    //get request Header Jwt
    public String resolveJwt(final HttpServletRequest request){
        final String header = request.getHeader(JwtAuthConstatns.AUTH_HEADER);

        if(header != null && header.startsWith(JwtAuthConstatns.AUTH_HEADER)){
            return header.split(" ")[1]; //AUTH_HEADER 제거
        }
        return null;
    }

    //create usernamePasswordAuthenticationToken
    public Authentication getAuthentication(final String jwt){
        final Account account = accountService.getAccount(String.valueOf(getSubject(jwt)));
        
        return new UsernamePasswordAuthenticationToken(account, "", account.getRoles().stream()
                                                                                        .map(Role::getValue)
                                                                                        .map(SimpleGrantedAuthority::new)
                                                                                        .collect(Collectors.toList()));
    }



    
    
}
