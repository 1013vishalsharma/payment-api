package com.hitpixel.payment.security;

import com.hitpixel.payment.domain.User;
import com.hitpixel.payment.dto.AuthenticationToken;
import com.hitpixel.payment.service.JWTService;
import com.hitpixel.payment.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomJWTAuthenticationProvider implements AuthenticationProvider {

    private final JWTService jwtService;

    private final UserService userService;

    public CustomJWTAuthenticationProvider(JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(AuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            log.info("Performing custom JWT authentication");
            if(authentication.isAuthenticated()) {
                log.info("JWT is authenticated");
                return authentication;
            }
            String token = (String) authentication.getCredentials();
            String email = jwtService.getEmailClaim(token);
            User user = userService.findUserByEmail(email);
            if(jwtService.validateJWT(token)) {
                log.info("JWT token is valid");
                ((AuthenticationToken) authentication).setUser(user);
                authentication.setAuthenticated(true);
                return authentication;
            } else {
                log.error("JWT token is invalid");
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthenticationToken.class);
    }
}
