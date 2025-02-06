package com.hitpixel.payment.integeration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitpixel.payment.dto.JWTAuthToken;
import com.hitpixel.payment.dto.LoginRequest;
import com.hitpixel.payment.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerIntegrationTest {

    private TestRestTemplate restTemplate= new TestRestTemplate();

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private LoginRequest loginRequest;

    private String url;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        user = new User("Arya Stark", "arya.stark@gmail.com", "1234567890");
        loginRequest = new LoginRequest("arya.stark@gmail.com", "1234567890");
        url = "http://localhost:" + port +"/api/users/";
    }

    @Test
    void testRegisterUser_Success() {
        ResponseEntity<Void> response = restTemplate.postForEntity(
                url + "register", user, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testLoginUser_Success() {

        restTemplate.postForEntity(url + "register", user, Void.class);

        ResponseEntity<JWTAuthToken> response = restTemplate.postForEntity(
                url + "login", loginRequest, JWTAuthToken.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testLoginUser_Failure_InvalidCredentials(){
        LoginRequest invalidLoginRequest = new LoginRequest("sam@gmail.com", "samspassword");

        restTemplate.postForEntity(url + "register", user, Void.class);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url + "login", invalidLoginRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("User does not exists in the system");
    }

    @Test
    void testRegisterUser_BadRequest(){
        User invalidUser = new User("", "sansa.stark@gmail.com", "password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                url + "register", invalidUser, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
