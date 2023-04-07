package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.domain.HttpResponse;
import com.github.valhio.storeapi.exception.domain.EmailExistException;
import com.github.valhio.storeapi.exception.domain.EmailNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.service.UserService;
import com.github.valhio.storeapi.utility.JWTTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private final Long USER_ID = 1L;
    private final String USER_FIRST_NAME = "John";
    private final String USER_LAST_NAME = "Doe";
    private final String USER_EMAIL = "leeroy@jenkins";
    private final String USER_PASSWORD = "123456";
    private final String EMAIL_ALREADY_EXISTS = "Email already exists";
    private final String USER_NOT_FOUND = "User not found";
    private final String ILLEGAL_ARGUMENT = "Illegal argument exception";
    private final UserNotFoundException userNotFoundException =
            new UserNotFoundException(this.USER_NOT_FOUND);
    private final EmailExistException emailExistException =
            new EmailExistException(this.EMAIL_ALREADY_EXISTS);
    private final IllegalArgumentException illegalArgumentException =
            new IllegalArgumentException(this.ILLEGAL_ARGUMENT);

    private User user;

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTTokenProvider jwtTokenProvider;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        this.user = new User();
        this.user.setId(USER_ID);
        this.user.setFirstName(USER_FIRST_NAME);
        this.user.setLastName(USER_LAST_NAME);
        this.user.setEmail(USER_EMAIL);
        this.user.setPassword(USER_PASSWORD);
    }

    @Nested
    @DisplayName("Register User")
    class RegisterUser {

        @Test
        void testRegisterUser() throws EmailExistException {
            when(userService.register(user)).thenReturn(user);

            ResponseEntity<HttpResponse> response = userController.register(user);
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
            assertEquals("User registered successfully", response.getBody().getMessage());
            assertNotNull(response.getBody().getTimeStamp());
            assertNotNull(response.getBody().getData());
            assertEquals(user, response.getBody().getData().get("user"));
        }

        @Test
        void testRegisterUserShouldThrowIfEmailAlreadyExists() throws EmailExistException {
            when(userService.register(user)).thenThrow(emailExistException);
            assertThrows(EmailExistException.class, () -> userController.register(user));
            verify(userService, times(1)).register(user);
        }

        @Test
        void testRegisterUserShouldThrowWithIllegalArgumentException() throws EmailExistException {
            when(userService.register(user)).thenThrow(illegalArgumentException);
            assertThrows(IllegalArgumentException.class, () -> userController.register(user));
            verify(userService, times(1)).register(user);
        }
    }

    @Nested
    @DisplayName("Login User")
    class LoginUser {

        @Test
        void testLoginUser() throws UserNotFoundException {
            UserPrincipal userPrincipal = new UserPrincipal(user);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Jwt-Token", "test-jwt-token");
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);
            when(authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(USER_EMAIL, USER_PASSWORD)))
                    .thenReturn(new UsernamePasswordAuthenticationToken(userPrincipal, null, Collections.emptyList()));
            when(jwtTokenProvider.generateJwtToken(ArgumentMatchers.any())).thenReturn("test-jwt-token");

            ResponseEntity<User> responseEntity = userController.login(user);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals(user, responseEntity.getBody());
            assertEquals(headers, responseEntity.getHeaders());
            assertEquals("test-jwt-token", responseEntity.getHeaders().get("Jwt-Token").get(0));
            verify(userService, times(1)).findUserByEmail(USER_EMAIL);
            verify(authenticationManager, times(1)).authenticate(
                    new UsernamePasswordAuthenticationToken(USER_EMAIL, USER_PASSWORD));
            verify(jwtTokenProvider, times(1)).generateJwtToken(ArgumentMatchers.any());
        }

        @Test
        void testLoginUserShouldThrowIfUserNotFound() throws UserNotFoundException {
            when(userService.findUserByEmail(USER_EMAIL)).thenThrow(userNotFoundException);
            assertThrows(UserNotFoundException.class, () -> userController.login(user));
            verify(userService, times(1)).findUserByEmail(USER_EMAIL);
        }
    }

    @Nested
    @DisplayName("Is JWT Token Valid")
    class isJwtValid {

        @Test
        void testIsJwtValid() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader("Authorization")).thenReturn("Bearer test-jwt-token");
            when(request.getHeader("Email")).thenReturn(USER_EMAIL);
            when(jwtTokenProvider.isTokenValid(USER_EMAIL, "test-jwt-token")).thenReturn(true);

            boolean jwtValid = userController.isJwtValid(request);

            assertTrue(jwtValid);
            verify(jwtTokenProvider, times(1)).isTokenValid(USER_EMAIL, "test-jwt-token");
        }
    }

    @Nested
    @DisplayName("Find User By Email")
    class FindUserByEmail {

        @Test
        void testFindUserByEmail() throws UserNotFoundException {
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);

            ResponseEntity<HttpResponse> responseEntity = userController.findUserByEmail(USER_EMAIL);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals(user, responseEntity.getBody().getData().get("user"));
            verify(userService, times(1)).findUserByEmail(USER_EMAIL);
        }

        @Test
        void testFindUserByEmailShouldThrowIfUserNotFound() throws UserNotFoundException {
            when(userService.findUserByEmail(USER_EMAIL)).thenThrow(userNotFoundException);
            assertThrows(UserNotFoundException.class, () -> userController.findUserByEmail(USER_EMAIL));
            verify(userService, times(1)).findUserByEmail(USER_EMAIL);
        }
    }

    @Nested
    @DisplayName("Fetch all users")
    class FetchAllUsers {

        @Test
        void testFetchAllUsers() {
            Page<User> page = new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1);
            when(userService.getUsers("", 0, 10)).thenReturn(page);

            ResponseEntity<HttpResponse> responseEntity = userController.getUsers(Optional.of(""), Optional.of(0), Optional.of(10));

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals(page, responseEntity.getBody().getData().get("usersPage"));
            verify(userService, times(1)).getUsers("", 0, 10);
        }
    }

    @Nested
    @DisplayName("Update User")
    class UpdateUser {

        @Test
        void testUpdateUser() throws UserNotFoundException, EmailExistException, EmailNotFoundException {
            User newUser = new User();
            when(userService.update(newUser, USER_EMAIL)).thenReturn(UserControllerTest.this.user);

            ResponseEntity<HttpResponse> responseEntity = userController.update(newUser, USER_EMAIL);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
            assertEquals(user, responseEntity.getBody().getData().get("user"));
            verify(userService, times(1)).update(newUser, USER_EMAIL);
        }

        @Test
        void testUpdateUserShouldThrowIfUserNotFound() throws UserNotFoundException, EmailExistException {
            when(userService.update(user,USER_EMAIL)).thenThrow(userNotFoundException);
            assertThrows(UserNotFoundException.class, () -> userController.update(user,USER_EMAIL));
            verify(userService, times(1)).update(user,USER_EMAIL);
        }

        @Test
        void testUpdateUserShouldThrowIfEmailAlreadyExists() throws UserNotFoundException, EmailExistException {
            when(userService.update(user, USER_EMAIL)).thenThrow(emailExistException);
            assertThrows(EmailExistException.class, () -> userController.update(user, USER_EMAIL));
            verify(userService, times(1)).update(user, USER_EMAIL);
        }
    }

}