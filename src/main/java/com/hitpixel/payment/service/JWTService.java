package com.hitpixel.payment.service;

import com.hitpixel.payment.exception.JWTFailureException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class JWTService {

    private static final SecretKey SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Method to generate JWT token
     * @param name claim info
     * @param email claim info
     * @return generated JWT token
     */
    public String generateJWTToken(String name, String email) {
        log.info("Generating JWT token");
        try {
            String jwt = Jwts
                    .builder()
                    .setClaims(Map.of(
                            "name", name,
                            "email", email))
                    .setSubject("payment-login")
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                    .signWith(SECRET)
                    .compact();
            log.info("Successfully generated JWT");
            return jwt;
        } catch(Exception exception) {
            log.error("Error encountered while generating JWT", exception);
            throw new JWTFailureException("Error encountered while generating JWT");
        }
    }

    /**
     * Method to extract Claims from JWT
     * @param token the JWT
     * @return Claims object from token
     */
    public Claims extractJWTClaims(String token) {
        log.info("Extracting claims from JWT");
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(SECRET)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception exception) {
            log.error("Encountered JWT error", exception);
            throw new JWTFailureException("JWT not valid");
        }
    }

    /**
     * Method to extract the email claim from JWT
     * @param token the JWT
     * @return email claim
     */
    public String getEmailClaim(String token) {
        log.info("Fetching email claim from JWT");
        return (String) extractJWTClaims(token).get("email");
    }

    /**
     * Method to validate JWT
     * @param token the JWT
     * @return boolean value if JWT is valid or not
     */
    public boolean validateJWT(String token) {
        log.info("Validating JWT");
        return extractJWTClaims(token).getExpiration().after(new Date());
    }
}
