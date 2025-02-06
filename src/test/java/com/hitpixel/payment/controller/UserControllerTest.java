package com.hitpixel.payment.controller;

import com.hitpixel.payment.dto.JWTAuthToken;
import com.hitpixel.payment.dto.LoginRequest;
import com.hitpixel.payment.dto.User;
import com.hitpixel.payment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private User testUser;
    private LoginRequest testLoginRequest;
    private JWTAuthToken testAuthToken;

    @BeforeEach
    void setUp() {
        testUser = new User("johneer", "abcrr@abcwe.com", "1234567822");
        testLoginRequest = new LoginRequest("abcrr@abcwe.com", "1234567822");
        testAuthToken = new JWTAuthToken("sample-token");
    }

    @Test
    void testRegisterUsers_Success() throws Exception {
        doNothing().when(userService).registerUsers(testUser);

        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content("""
                                        {
                                            "name": "johneer",
                                            "email": "abcrr@abcwe.com",
                                            "password": "1234567822"
                                        }
                                        """))
                .andExpect(status().isCreated());

        verify(userService, times(1)).registerUsers(testUser);
    }

    @Test
    void testLogin_Success() throws Exception {
        when(userService.loginUser(testLoginRequest)).thenReturn(testAuthToken);

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content("""
                                        {
                                            "email": "abcrr@abcwe.com",
                                            "password": "1234567822"
                                        }"""
                                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("sample-token"))
                .andReturn();

        verify(userService, times(1)).loginUser(testLoginRequest);
    }

    @Test
    void testRegisterUsers_Failure_NameNotPresent() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "email": "abcrr@abcwe.com",
                                    "password": "1234567822"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterUsers_Failure_NameEmpty() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": ""
                                    "email": "abcrr@abcwe.com",
                                    "password": "1234567822"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
