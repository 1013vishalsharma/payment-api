package com.hitpixel.payment.security;

import com.hitpixel.payment.domain.User;
import com.hitpixel.payment.dto.AuthenticationToken;
import com.hitpixel.payment.service.JWTService;
import com.hitpixel.payment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomJWTAuthenticationProviderTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomJWTAuthenticationProvider customJWTAuthenticationProvider;

    private AuthenticationToken authenticationToken;
    private String token;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        token = "random-jwt-token-ewhfiuwhuiwhifuhauifhuwhfur4r8438u83u5983u9t34t93utaofjoifjsfjwjf8983u93u98u893uf89u89u89uw";
        user = new User("1", "Geoffrey", "geoffrey@gmail.com", "password123");
        authenticationToken = new AuthenticationToken(token);
    }

    @Test
    void testAuthenticateWithValidToken() {
        when(jwtService.getEmailClaim(token)).thenReturn("geoffrey@gmail.com");
        when(userService.findUserByEmail("geoffrey@gmail.com")).thenReturn(user);
        when(jwtService.validateJWT(token)).thenReturn(true);

        AuthenticationToken result = (AuthenticationToken) customJWTAuthenticationProvider.authenticate(authenticationToken);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals(user, result.getUser());
        verify(jwtService, times(1)).getEmailClaim(token);
        verify(userService, times(1)).findUserByEmail("geoffrey@gmail.com");
    }

    @Test
    void testAuthenticateWithInvalidToken() {
        when(jwtService.getEmailClaim(token)).thenReturn("geoffrey@gmail.com");
        when(userService.findUserByEmail("geoffrey@gmail.com")).thenReturn(user);
        when(jwtService.validateJWT(token)).thenReturn(false);

        AuthenticationToken result = (AuthenticationToken) customJWTAuthenticationProvider.authenticate(authenticationToken);

        assertNull(result); // Should return null since token validation failed
        verify(jwtService, times(1)).getEmailClaim(token);
        verify(userService, times(1)).findUserByEmail("geoffrey@gmail.com");
    }

    @Test
    void testAuthenticateWithAlreadyAuthenticatedToken() {
        authenticationToken.setAuthenticated(true);

        AuthenticationToken result = (AuthenticationToken) customJWTAuthenticationProvider.authenticate(authenticationToken);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        verifyNoInteractions(jwtService, userService);
    }

    @Test
    void testAuthenticateWithInvalidAuthenticationType() {
        Authentication invalidAuthentication = mock(Authentication.class);

        Authentication result = customJWTAuthenticationProvider.authenticate(invalidAuthentication);

        assertNull(result);
    }

    @Test
    void testSupports() {
        Class<?> authenticationClass = AuthenticationToken.class;

        boolean result = customJWTAuthenticationProvider.supports(authenticationClass);

        assertTrue(result);
    }

    @Test
    void testDoesNotSupportOtherAuthenticationTypes() {
        Class<?> authenticationClass = UsernamePasswordAuthenticationToken.class;

        boolean result = customJWTAuthenticationProvider.supports(authenticationClass);

        assertFalse(result);
    }
}
