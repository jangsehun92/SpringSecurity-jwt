package newbee.jsh.security_jwt.account.dto.response;

import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import newbee.jsh.security_jwt.account.entity.Role;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class ResponseAccountDto {

    private String email;
    private Set<Role> roles;
    
}
