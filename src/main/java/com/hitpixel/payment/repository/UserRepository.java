package com.hitpixel.payment.repository;

import com.hitpixel.payment.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Check if user exists with given name and password
     * @param name user's name
     * @param password user's password
     * @return boolean value if user exists or not
     */
    boolean existsByNameAndPassword(String name, String password);

    /**
     * Check if user exists with given email
     * @param email email of the user
     * @return boolean value if user exists or not
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by email
     * @param email user's email
     * @return user details
     */
    Optional<User> findByEmail(String email);
}
