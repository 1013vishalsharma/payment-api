package com.hitpixel.payment.repository;

import com.hitpixel.payment.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("1", "Johnnie", "johnnie@gmail.com", "12345678");
        userRepository.save(user);
    }

    @Test
    void testExistsByNameAndPassword() {
        boolean exists = userRepository.existsByNameAndPassword("Johnnie", "12345678");
        assertTrue(exists);
    }

    @Test
    void testExistsByNameAndPassword_whenNameDoesNotExists() {
        boolean exists = userRepository.existsByNameAndPassword("John", "12345678");
        assertFalse(exists);
    }

    @Test
    void testExistsByEmail() {
        boolean exists = userRepository.existsByEmail("johnnie@gmail.com");
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_whenEmailDoesNotExists() {
        boolean notExists = userRepository.existsByEmail("abc@abc.com");
        assertFalse(notExists);
    }

    @Test
    void testFindByEmail() {
        Optional<User> userOptional = userRepository.findByEmail("johnnie@gmail.com");
        assertTrue(userOptional.isPresent());
        assertEquals(user.getName(), userOptional.get().getName());
        assertEquals(user.getEmail(), userOptional.get().getEmail());
    }

    @Test
    void testFindByEmail_whenEmailDoesNotExists() {
        Optional<User> nonExistentUser = userRepository.findByEmail("abc@gmail.com");
        assertFalse(nonExistentUser.isPresent());
    }

    @Test
    void testSaveAndRetrieveUser() {
        User newUser = new User("2", "Jane", "jane@gmail.com", "password123");
        userRepository.save(newUser);

        Optional<User> retrievedUser = userRepository.findByEmail("jane@gmail.com");
        assertTrue(retrievedUser.isPresent());
        assertEquals("Jane", retrievedUser.get().getName());
    }
}
