package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.domain.EmailExistException;
import com.github.valhio.storeapi.exception.domain.PasswordNotMatchException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.repository.UserRepository;
import com.github.valhio.storeapi.service.LoginAttemptService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Test get users")
    void testGetUsers() {
        String keyword = "test";
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        User user = new User();
        user.setId(1L);
        user.setEmail("testuser");
        Page<User> pageResult = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(keyword, pageRequest)).thenReturn(pageResult);

        Page<User> result = userService.getUsers(keyword, page, size);

        Assertions.assertEquals(pageResult, result);
        verify(userRepository).findAll(keyword, pageRequest);
    }

    @Nested
    @DisplayName("Test load user by username")
    class TestLoadUserByUsername {

        @Test
        void testLoadUserByUsername() {
            String email = "john.doe@example.com";
            User user = new User();
            user.setId(1L);
            user.setEmail(email);
            user.setPassword("password");
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);

            UserDetails userDetails = userService.loadUserByUsername(email);

            assertNotNull(userDetails);
            assertEquals(user.getEmail(), userDetails.getUsername());
            assertEquals(user.getPassword(), userDetails.getPassword());
            verify(userRepository, times(1)).save(user);
            verify(userRepository, times(1)).findByEmail(email);
        }

        @Test
        void testLoadUserByUsernameWithInvalidEmail() {
            String email = "john.doe@example.com";

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () -> {
                userService.loadUserByUsername(email);
            });
        }
    }

}
