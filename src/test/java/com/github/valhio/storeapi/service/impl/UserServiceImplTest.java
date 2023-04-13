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

    @Nested
    @DisplayName("Test register")
    class TestRegister {

        @Test
        @DisplayName("Test register should succeed")
        void testRegister() throws EmailExistException {
            User user = new User();
            user.setId(1L);
            user.setEmail("leeroy");
            user.setPassword("password");

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            User res = userService.register(user);

            assertEquals(user, res);
            verify(userRepository, times(1)).existsByEmail(anyString());
            verify(passwordEncoder, times(1)).encode(anyString());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Test register should throw EmailExistException")
        void testRegisterWithExistingEmail() {
            User user = new User();
            user.setId(1L);
            user.setEmail("leeroy");
            user.setPassword("password");

            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            assertThrows(EmailExistException.class, () -> {
                userService.register(user);
            });

            verify(userRepository, times(1)).existsByEmail(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Test register should throw IllegalArgumentException if no email is passed")
        void testRegisterWithInvalidEmail() {
            User user = new User();
            user.setId(1L);
            user.setEmail("");
            user.setPassword("password");

            assertThrows(IllegalArgumentException.class, () -> {
                userService.register(user);
            });

            verify(userRepository, never()).existsByEmail(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Test update user")
    class UpdateUser {
        @Test
        @DisplayName("Test update user should succeed when email is not changed")
        void testUpdateUser() throws EmailExistException, UserNotFoundException {
            User newUser = new User();
            newUser.setId(1L);
            newUser.setEmail("NOT leeroy");
            newUser.setPassword("password");
            newUser.setRole(Role.ROLE_USER);
            newUser.setAuthorities(Role.ROLE_USER.getAuthorities());

            User user = new User();
            user.setId(1L);
            user.setEmail("leeroy");
            user.setPassword("password");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            User res = userService.update(newUser, user.getEmail());

            assertEquals(newUser, res);
            verify(userRepository, times(1)).findByEmail(anyString());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Test update user should throw UserExistsException when email is changed to an existing email")
        void testUpdateUserShouldThrowWhenNewEmailAlreadyExists() throws EmailExistException, UserNotFoundException {
            User newUser = new User();
            newUser.setId(1L);
            newUser.setEmail("NOT leeroy");
            newUser.setPassword("password");
            newUser.setRole(Role.ROLE_USER);
            newUser.setAuthorities(Role.ROLE_USER.getAuthorities());

            User user = new User();
            user.setId(1L);
            user.setEmail("leeroy");
            user.setPassword("password");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);

            assertThrows(EmailExistException.class, () -> {
                userService.update(newUser, user.getEmail());
            });

            verify(userRepository, times(1)).existsByEmail(anyString());
            verify(userRepository, times(1)).findByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Test update user should throw UserNotFoundException when user is not found")
        void testUpdateUserShouldThrowWhenUserNotFound() throws EmailExistException, UserNotFoundException {
            User newUser = new User();
            newUser.setId(1L);
            newUser.setEmail("NOT leeroy");
            newUser.setPassword("password");
            newUser.setRole(Role.ROLE_USER);
            newUser.setAuthorities(Role.ROLE_USER.getAuthorities());

            User user = new User();
            user.setId(1L);
            user.setEmail("leeroy");
            user.setPassword("password");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> {
                userService.update(newUser, user.getEmail());
            });

            verify(userRepository, times(1)).findByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

    }

    @Nested
    @DisplayName("Test delete by Id")
    class DeleteById {

        @Test
        @DisplayName("Test delete user")
        void testDeleteUser() throws UserNotFoundException {
            User user = new User();
            user.setId(1L);
            user.setEmail("leeroy");
            user.setPassword("password");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());

            when(userRepository.findById(any())).thenReturn(Optional.of(user));
            doNothing().when(userRepository).delete(any(User.class));

            userService.delete(user.getId());

            verify(userRepository, times(1)).findById(any());
            verify(userRepository, times(1)).delete(any(User.class));
        }

        @Test
        @DisplayName("Test delete should not delete if user does not exist")
        void testDeleteUserShouldNotDeleteIfUserDoesNotExist() throws UserNotFoundException {
            User user = new User();
            user.setId(1L);
            user.setEmail("leeroy");
            user.setPassword("password");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());

            when(userRepository.findById(any())).thenReturn(Optional.empty());

            userService.delete(user.getId());

            verify(userRepository, times(1)).findById(any());
            verify(userRepository, never()).delete(any(User.class));
        }
    }

    @Nested
    @DisplayName("Test delete by userId")
    class DeleteByUserId {

        @Test
        @DisplayName("Test delete user by userId")
        void testDeleteUser() throws UserNotFoundException {
            User user = new User();
            user.setUserId("1");
            user.setEmail("leeroy");
            user.setPassword("password");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());

            when(userRepository.findByUserId(any())).thenReturn(Optional.of(user));
            doNothing().when(userRepository).delete(any(User.class));

            userService.deleteByUserId(user.getUserId());

            verify(userRepository, times(1)).findByUserId(any());
            verify(userRepository, times(1)).delete(any(User.class));
        }

        @Test
        @DisplayName("Test delete should not delete if user does not exist")
        void testDeleteUserShouldNotDeleteIfUserDoesNotExist() throws UserNotFoundException {
            User user = new User();
            user.setUserId("1");
            user.setEmail("leeroy");
            user.setPassword("password");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());

            when(userRepository.findByUserId(any())).thenReturn(Optional.empty());

            userService.deleteByUserId(user.getUserId());

            verify(userRepository, times(1)).findByUserId(any());
            verify(userRepository, never()).delete(any(User.class));
        }
    }

    @Nested
    @DisplayName("Test find user by email")
    class FindByEmail {

        @Test
        @DisplayName("Test find user by email")
        void testFindUserByEmail() throws UserNotFoundException {
            User user = new User();
            user.setId(1L);
            user.setEmail("leeroy");
            user.setPassword("password");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            User res = userService.findUserByEmail(user.getEmail());

            assertEquals(user, res);
            verify(userRepository, times(1)).findByEmail(anyString());
        }

        @Test
        @DisplayName("Test find user by email should throw UserNotFoundException when user is not found")
        void testFindUserByEmailShouldThrowWhenUserNotFound() throws UserNotFoundException {
            User user = new User();
            user.setId(1L);
            user.setEmail("leeroy");
            user.setPassword("password");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> {
                userService.findUserByEmail(user.getEmail());
            });

            verify(userRepository, times(1)).findByEmail(anyString());
        }
    }

    @Nested
    @DisplayName("Test find user by userId")
    class FindByUserId {

        @Test
        @DisplayName("Test find user by userId")
        void testFindUserByUserId() throws UserNotFoundException {
            User user = new User();
            user.setUserId("1");
            user.setEmail("leeroy");
            user.setPassword("password");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());

            when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));

            User res = userService.findUserByUserId(user.getUserId());

            assertEquals(user, res);
            verify(userRepository, times(1)).findByUserId(anyString());
        }

        @Test
        @DisplayName("Test find user by userId should throw UserNotFoundException when user is not found")
        void testFindUserByUserIdShouldThrowWhenUserNotFound() throws UserNotFoundException {
            User user = new User();
            user.setUserId("1");
            user.setEmail("leeroy");
            user.setPassword("password");
            user.setRole(Role.ROLE_USER);
            user.setAuthorities(Role.ROLE_USER.getAuthorities());

            when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> {
                userService.findUserByUserId(user.getUserId());
            });

            verify(userRepository, times(1)).findByUserId(anyString());
        }
    }

}
