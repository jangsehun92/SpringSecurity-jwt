package newbee.jsh.security_jwt.config.jwt;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import newbee.jsh.security_jwt.auth.repository.AuthBlackListRepository;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private SecretKey key;

    private final AccountService accountService;

    private final AuthBlackListRepository authBlackListRepository;

    @PostConstruct
    private void init(){
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    //create AccessToken
    public String createAccessToken(final String email){
        final Claims claims = Jwts.claims().setSubject(email);
        /**
         * claims.put("key", value); 
         * 위처럼 claims내에 추가적인 정보를 넣을 수 있다.
         * 
         * claims()의 get("key")를 통해 접근할 수 있을 것으로 예상한다. UNIT TEST 필요 
         */
        final Date regDate = new Date();
        final Date expirationDate = new Date(regDate.getTime() + Duration.ofMinutes(30).toMillis());

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
        final Claims claims = Jwts.claims().setSubject(value);
        final Date regDate = new Date();
        final Date expirationDate = new Date(regDate.getTime() + Duration.ofDays(1).toMillis());

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
    public boolean isBlackList(final String accessToken){
        final String email = getSubject(accessToken);

        if(authBlackListRepository.findByEmailAndAccessToken(email, accessToken) != null){
            return true;
        }
        return false;
    }

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

        if(header != null && header.startsWith(JwtAuthConstatns.TOKEN_TYPE)){
            return header.split(" ")[1]; //AUTH_HEADER(BEARER ) 제거
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
