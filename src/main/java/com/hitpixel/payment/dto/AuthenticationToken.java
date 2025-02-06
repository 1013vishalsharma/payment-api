package com.hitpixel.payment.dto;

import com.hitpixel.payment.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

public class AuthenticationToken extends AbstractAuthenticationToken {

    @Setter
    private String principle;
    private String credentials;

    @Setter
    @Getter
    private User user;

    public AuthenticationToken(String credentials){
        super(List.of());
        this.credentials = credentials;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principle;
    }
}
