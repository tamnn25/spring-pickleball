package com.pickleball.springdemo;

import com.pickleball.springdemo.model.User;
import com.pickleball.springdemo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUserToDatabase() {
        // Given
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");

        // When
        User savedUser = userRepository.save(user);

        // Then
//        assertThat(savedUser).isNotNull();
//        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("testuser@example.com");
    }
}
