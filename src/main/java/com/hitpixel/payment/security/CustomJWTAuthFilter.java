package com.hitpixel.payment.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hitpixel.payment.dto.AuthenticationToken;
import com.hitpixel.payment.dto.ErrorDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class CustomJWTAuthFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    public CustomJWTAuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null && new AntPathRequestMatcher("/api/payments/**").matches(request)) {
            try {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if(authorizationHeader != null) {
                String authToken = authorizationHeader.replace("Bearer ", "").trim();
                authentication = new AuthenticationToken(authToken);
            } else {
                log.error("Auth token not present");
                throw new AuthenticationException("Auth Token not present");
            }
                Authentication authenticated = authenticationManager.authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(authenticated);
                log.info("Request authenticated successfully");
            } catch (Exception exception) {
                log.error("encountered exception authenticating JWT", exception);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ErrorDetails errorDetails = new ErrorDetails(
                        exception.getMessage(),
                        HttpStatus.UNAUTHORIZED.value(),
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.name());
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                byte[] body = objectMapper.writeValueAsBytes(errorDetails);
                response.getOutputStream().write(body);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
