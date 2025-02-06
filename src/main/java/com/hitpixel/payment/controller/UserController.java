package com.hitpixel.payment.controller;

import com.hitpixel.payment.dto.ErrorDetails;
import com.hitpixel.payment.dto.JWTAuthToken;
import com.hitpixel.payment.dto.LoginRequest;
import com.hitpixel.payment.dto.User;
import com.hitpixel.payment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register user with the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered user into the system"),
            @ApiResponse(responseCode = "400", description = "Request body is incorrect",content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "500", description = "Error occurred while processing the request", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping("register")
    public ResponseEntity<Void> registerUsers(@Valid @RequestBody User user) {
        log.info("Registering user={} with the system", user.email());
        userService.registerUsers(user);
        log.info("Successfully registered user={} with the system", user.email());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @Operation(summary = "Login user into the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged into the system"),
            @ApiResponse(responseCode = "401", description = "Username or password is incorrect", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "User does not exists in the system", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "Request body is incorrect", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "500", description = "Error occurred while processing the request", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping("login")
    public ResponseEntity<JWTAuthToken> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Logging in user with email={}", loginRequest.email());
        JWTAuthToken jwtAuthToken = userService.loginUser(loginRequest);
        log.info("User={} successfully logged into the system", loginRequest.email());
        return ResponseEntity.ok(jwtAuthToken);
    }
}
