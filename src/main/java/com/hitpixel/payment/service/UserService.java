package com.hitpixel.payment.service;

import com.hitpixel.payment.dto.JWTAuthToken;
import com.hitpixel.payment.dto.LoginRequest;
import com.hitpixel.payment.dto.User;
import com.hitpixel.payment.exception.UserAlreadyExistsException;
import com.hitpixel.payment.exception.UserNotFoundException;
import com.hitpixel.payment.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JWTService jwtService;

    public UserService(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
     * Method to register users into the system
     * @param user user to be registered
     */
    public void registerUsers(User user) {
        log.info("Initiated registration of user={}", user.email());

        if(userRepository.findByEmail(user.email()).isPresent()) {
            log.error("User with email={} already exists in the system", user.email());
            throw new UserAlreadyExistsException("User already registered.");
        }
        com.hitpixel.payment.domain.User domainUser = new com.hitpixel.payment.domain.User();
        domainUser.setId(UUID.randomUUID().toString());
        domainUser.setName(user.name());
        domainUser.setEmail(user.email());
        domainUser.setPassword(user.password());
        userRepository.save(domainUser);
        log.info("Registration completed for user={}", user.email());
    }

    /**
     * Method to authenticate the user
     * @param loginRequest email and password of the user
     * @return generated JWT token
     */
    public JWTAuthToken loginUser(LoginRequest loginRequest) {
        log.info("Initiating login process for user={}", loginRequest.email());
        com.hitpixel.payment.domain.User user = findUserByEmail(loginRequest.email());
        if(loginRequest.password().equals(user.getPassword())) {
            log.info("User with email={} logged in successfully", loginRequest.email());
            return new JWTAuthToken(jwtService.generateJWTToken(user.getName(), user.getEmail()));
        }
        log.error("email or password does not match");
        throw new BadCredentialsException("Login details are incorrect");
    }

    /**
     * Method to check if user exists in the system via email
     * @param  email email of the user
     * @return
     */
    public boolean checkUserExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Method to find User from the system by email id
     * @param email email id of the user
     * @return fetched user object
     */
    public com.hitpixel.payment.domain.User findUserByEmail(String email) {
        log.info("Fetching user for email={}", email);
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Could not find user={} in db", email);
                    throw new UserNotFoundException("User does not exists in the system");
                });
    }
}
