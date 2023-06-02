package example.security.jwt;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String message,Throwable t){
        super(message, t);
    }
    public JwtAuthenticationException(String message) {
        super(message);
    }
}
