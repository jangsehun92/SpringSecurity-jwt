package newbee.jsh.security_jwt.global.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import newbee.jsh.security_jwt.config.jwt.JwtProvider;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //request에서 jwt을 꺼낸다
        final String accessToken = jwtProvider.resolveJwt(request);

        //만약 토큰이 null이 아니면서, JWT가 유효하다면 
        if(accessToken != null && jwtProvider.dateValid(accessToken) && !jwtProvider.isBlackList(accessToken)){
            //Authentication authentication 객체에 jwt를 이용하여 넣어준다.
            Authentication authentication = jwtProvider.getAuthentication(accessToken);
            //SecurityContextHolder.getContext().setAuthentication(authentication)을 넣어준다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        //다음 filter 진행
        filterChain.doFilter(request, response);
        
    }
    
}
