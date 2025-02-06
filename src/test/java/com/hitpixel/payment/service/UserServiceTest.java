package com.hitpixel.payment.service;

import com.hitpixel.payment.dto.JWTAuthToken;
import com.hitpixel.payment.dto.LoginRequest;
import com.hitpixel.payment.dto.User;
import com.hitpixel.payment.exception.UserAlreadyExistsException;
import com.hitpixel.payment.exception.UserNotFoundException;
import com.hitpixel.payment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTService jwtService;

    private User user;
    private LoginRequest loginRequest;

    private com.hitpixel.payment.domain.User domainUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("Ned Stark", "ned.stark@gmail.com", "password123");
        loginRequest = new LoginRequest("ned.stark@gmail.com", "password123");
    }

    @Test
    void testRegisterUsers() {
        domainUser = new com.hitpixel.payment.domain.User("1", "Ned Stark", "ned.stark@gmail.com", "password123");
        when(userRepository.save(domainUser)).thenReturn(domainUser);

        userService.registerUsers(user);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testRegisterUsers_withRegisteredUser() {
        domainUser = new com.hitpixel.payment.domain.User("1", "Ned Stark", "ned.stark@gmail.com", "password123");
        when(userRepository.findByEmail(user.email())).thenReturn(Optional.of(domainUser));

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUsers(user));

        verify(userRepository, times(0)).save(any());
    }

    @Test
    void testLoginUserSuccess() {
        com.hitpixel.payment.domain.User savedUser = new com.hitpixel.payment.domain.User("1", user.name(), user.email(), user.password());
        when(userRepository.findByEmail(user.email())).thenReturn(Optional.of(savedUser));
        when(jwtService.generateJWTToken(savedUser.getName(), savedUser.getEmail())).thenReturn("generated_jwt_token");

        JWTAuthToken jwtAuthToken = userService.loginUser(loginRequest);

        assertNotNull(jwtAuthToken);
        assertEquals("generated_jwt_token", jwtAuthToken.token());
        verify(userRepository, times(1)).findByEmail(user.email());
        verify(jwtService, times(1)).generateJWTToken(savedUser.getName(), savedUser.getEmail());
    }

    @Test
    void testLoginUserFailureIncorrectPassword() {
        com.hitpixel.payment.domain.User savedUser = new com.hitpixel.payment.domain.User("1", user.name(), user.email(), "wrongpassword");
        when(userRepository.findByEmail(user.email())).thenReturn(Optional.of(savedUser));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> userService.loginUser(loginRequest));
        assertEquals("Login details are incorrect", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(user.email());
    }

    @Test
    void testCheckUserExistsTrue() {
        when(userRepository.existsByEmail(user.email())).thenReturn(true);

        boolean exists = userService.checkUserExists(user.email());

        assertTrue(exists);
        verify(userRepository, times(1)).existsByEmail(user.email());
    }

    @Test
    void testCheckUserExistsFalse() {
        when(userRepository.existsByEmail(user.email())).thenReturn(false);

        boolean exists = userService.checkUserExists(user.email());

        assertFalse(exists);
        verify(userRepository, times(1)).existsByEmail(user.email()); // Ensure existsByEmail is called once
    }

    @Test
    void testFindUserByEmailSuccess() {
        com.hitpixel.payment.domain.User savedUser = new com.hitpixel.payment.domain.User("1", user.name(), user.email(), user.password());
        when(userRepository.findByEmail(user.email())).thenReturn(Optional.of(savedUser));

        com.hitpixel.payment.domain.User foundUser = userService.findUserByEmail(user.email());

        assertNotNull(foundUser);
        assertEquals(user.email(), foundUser.getEmail());
        verify(userRepository, times(1)).findByEmail(user.email());
    }

    @Test
    void testFindUserByEmailNotFound() {
        when(userRepository.findByEmail(user.email())).thenReturn(Optional.empty());
        String email = user.email();
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.findUserByEmail(email));
        assertEquals("User does not exists in the system", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(user.email());
    }
}
