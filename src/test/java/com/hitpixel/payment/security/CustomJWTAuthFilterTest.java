package com.hitpixel.payment.security;

import com.hitpixel.payment.dto.AuthenticationToken;
import com.hitpixel.payment.exception.JWTFailureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomJWTAuthFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CustomJWTAuthFilter customJWTAuthFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        request.setServletPath("/api/payments");
        response = new MockHttpServletResponse();
    }

    @Test
    void testDoFilterInternalWithValidJWT() throws ServletException, IOException {
        String token = "valid-jwt-token-lcsdknkjncksncksncksnkcjsndfjnsjfdnj832ur892u3489r2u89r239u298ru298ru289u3298u2";
        request.addHeader("Authorization", "Bearer " + token);
        request.setServletPath("/api/payments");
        Authentication mockAuth = new AuthenticationToken(token);
        AuthenticationToken authenticationToken = new AuthenticationToken(token);

        when(authenticationManager.authenticate(authenticationToken)).thenReturn(mockAuth);

        customJWTAuthFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationManager, times(1)).authenticate(authenticationToken);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithoutJWT() throws ServletException, IOException {
        request.addHeader("Authorization", "");

        customJWTAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithNoAuthorizationHeader() throws ServletException, IOException {
        request.removeHeader("Authorization");

        customJWTAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithInvalidJWT() throws ServletException, IOException {
        String token = "invalid-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);
        AuthenticationToken authenticationToken = new AuthenticationToken(token);

        when(authenticationManager.authenticate(authenticationToken)).thenThrow(new JWTFailureException("Invalid JWT"));

        customJWTAuthFilter.doFilterInternal(request, response, filterChain);
        verify(authenticationManager, times(1)).authenticate(authenticationToken);
        verify(filterChain, times(0)).doFilter(request, response);
    }
}
