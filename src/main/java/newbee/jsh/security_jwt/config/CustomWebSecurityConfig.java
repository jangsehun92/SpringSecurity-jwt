package newbee.jsh.security_jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import newbee.jsh.security_jwt.config.jwt.JwtProvider;
import newbee.jsh.security_jwt.global.filter.JwtAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class CustomWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtProvider jwtProvider;
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(jwtProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authorizeRequests()
                    .antMatchers(
                        "/api/account",
                        "/api/auth/login",
                        "/api/auth/reissuance"
                    ).permitAll()
                    .antMatchers(
                        "/api/user"
                    ).hasRole("USER")
                    .antMatchers(
                        "/api/admin"
                    ).hasRole("ADMIN")
            .and()
                .formLogin().disable()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

}
