package com.github.valhio.storeapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valhio.storeapi.config.SecurityConfiguration;
import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.domain.EmailExistException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.filter.JWTAccessDeniedHandler;
import com.github.valhio.storeapi.filter.JWTAuthenticationEntryPoint;
import com.github.valhio.storeapi.filter.JWTAuthorizationFilter;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.service.UserService;
import com.github.valhio.storeapi.utility.JWTTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@ExtendWith(SpringExtension.class)
//@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
//@SpringBootTest
//@Import(SecurityConfiguration.class)
@Import({SecurityConfiguration.class, JWTAuthorizationFilter.class, JWTAccessDeniedHandler.class, JWTAuthenticationEntryPoint.class})
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc()
//@ExtendWith(SpringExtension.class)
//@WebAppConfiguration
class UserControllerTest {

    private final Long USER_ID = 1234L;
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

    @MockBean
    private UserService userService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.user = new User();
        this.user.setId(USER_ID);
        this.user.setFirstName(USER_FIRST_NAME);
        this.user.setLastName(USER_LAST_NAME);
        this.user.setEmail(USER_EMAIL);
        this.user.setPassword(USER_PASSWORD);
        this.user.setRole(Role.ROLE_USER);
        this.user.setAuthorities(new String[]{"READ"});
    }

//    @Nested
//    @DisplayName("Register User")
//    class RegisterUser {
//
//        @Test
//        @WithMockUser(username = "leeroy@jenkins", authorities = {"READ"})
//        void testRegisterUser() throws Exception {
//            when(userService.register(any())).thenReturn(user);
//            String json = objectMapper.writeValueAsString(user);
//            mockMvc.perform(post("/api/v1/user/register").content(json).contentType("application/json"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.data.user.email", is(user.getEmail())))
//                    .andExpect(jsonPath("$.data.user.firstName", is(user.getFirstName())))
//                    .andExpect(jsonPath("$.data.user.lastName", is(user.getLastName())))
//                    .andExpect(jsonPath("$.data.user.role", is(user.getRole().name())))
//                    .andExpect(jsonPath("$.data.user.authorities", containsInAnyOrder("READ")));
//            verify(userService, times(1)).register(any());
//        }
//
//        @Test
//        void testRegisterUserShouldThrowIfEmailAlreadyExists() throws Exception {
//            when(userService.register(any())).thenThrow(emailExistException);
//            String json = objectMapper.writeValueAsString(user);
//
//            mockMvc.perform(post("/api/v1/user/register").content(json).contentType("application/json"))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
//                    .andExpect(jsonPath("$.message", is("Email already exists")));
//
//            verify(userService, times(1)).register(any());
//        }
//
//    }

//    @Nested
//    @DisplayName("Login User")
//    class LoginUser {
//
//        @Test
//        void testLoginUser() throws UserNotFoundException {
//            UserPrincipal userPrincipal = new UserPrincipal(user);
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Jwt-Token", "test-jwt-token");
//            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);
//            when(authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(USER_EMAIL, USER_PASSWORD)))
//                    .thenReturn(new UsernamePasswordAuthenticationToken(userPrincipal, null, Collections.emptyList()));
//            when(jwtTokenProvider.generateJwtToken(ArgumentMatchers.any())).thenReturn("test-jwt-token");
//
//            ResponseEntity<User> responseEntity = userController.login(user);
//
//            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//            assertEquals(user, responseEntity.getBody());
//            assertEquals(headers, responseEntity.getHeaders());
//            assertEquals("test-jwt-token", responseEntity.getHeaders().get("Jwt-Token").get(0));
//            verify(userService, times(1)).findUserByEmail(USER_EMAIL);
//            verify(authenticationManager, times(1)).authenticate(
//                    new UsernamePasswordAuthenticationToken(USER_EMAIL, USER_PASSWORD));
//            verify(jwtTokenProvider, times(1)).generateJwtToken(ArgumentMatchers.any());
//        }
//
//        @Test
//        void testLoginUserShouldThrowIfUserNotFound() throws UserNotFoundException {
//            when(userService.findUserByEmail(USER_EMAIL)).thenThrow(userNotFoundException);
//            assertThrows(UserNotFoundException.class, () -> userController.login(user));
//            verify(userService, times(1)).findUserByEmail(USER_EMAIL);
//        }
//    }
//
//    @Nested
//    @DisplayName("Is JWT Token Valid")
//    class isJwtValid {
//
//        @Test
//        void testIsJwtValid() {
//            HttpServletRequest request = mock(HttpServletRequest.class);
//            when(request.getHeader("Authorization")).thenReturn("Bearer test-jwt-token");
//            when(request.getHeader("Email")).thenReturn(USER_EMAIL);
//            when(jwtTokenProvider.isTokenValid(USER_EMAIL, "test-jwt-token")).thenReturn(true);
//
//            boolean jwtValid = userController.isJwtValid(request);
//
//            assertTrue(jwtValid);
//            verify(jwtTokenProvider, times(1)).isTokenValid(USER_EMAIL, "test-jwt-token");
//        }
//    }
//
//    @Nested
//    @DisplayName("Find User By Email")
//    class FindUserByEmail {
//
//        @Test
//        void testFindUserByEmail() throws UserNotFoundException {
//            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);
//
//            ResponseEntity<HttpResponse> responseEntity = userController.findUserByEmail(USER_EMAIL);
//
//            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//            assertEquals(user, responseEntity.getBody().getData().get("user"));
//            verify(userService, times(1)).findUserByEmail(USER_EMAIL);
//        }
//
//        @Test
//        void testFindUserByEmailShouldThrowIfUserNotFound() throws UserNotFoundException {
//            when(userService.findUserByEmail(USER_EMAIL)).thenThrow(userNotFoundException);
//            assertThrows(UserNotFoundException.class, () -> userController.findUserByEmail(USER_EMAIL));
//            verify(userService, times(1)).findUserByEmail(USER_EMAIL);
//        }
//    }
//
//    @Nested
//    @DisplayName("Fetch all users")
//    class FetchAllUsers {
//
//        @Test
//        void testFetchAllUsers() {
//            Page<User> page = new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1);
//            when(userService.getUsers("", 0, 10)).thenReturn(page);
//
//            ResponseEntity<HttpResponse> responseEntity = userController.getUsers(Optional.of(""), Optional.of(0), Optional.of(10));
//
//            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//            assertEquals(page, responseEntity.getBody().getData().get("usersPage"));
//            verify(userService, times(1)).getUsers("", 0, 10);
//        }
//    }
//
//    @Nested
//    @DisplayName("Update User")
//    class UpdateUser {
//
//        @Test
//        void testUpdateUser() throws UserNotFoundException, EmailExistException, EmailNotFoundException {
//            User newUser = new User();
//            when(userService.update(newUser, USER_EMAIL)).thenReturn(UserControllerTest.this.user);
//
//            ResponseEntity<HttpResponse> responseEntity = userController.update(newUser, USER_EMAIL);
//
//            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//            assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
//            assertEquals(user, responseEntity.getBody().getData().get("user"));
//            verify(userService, times(1)).update(newUser, USER_EMAIL);
//        }
//
//        @Test
//        void testUpdateUserShouldThrowIfUserNotFound() throws UserNotFoundException, EmailExistException {
//            when(userService.update(user, USER_EMAIL)).thenThrow(userNotFoundException);
//            assertThrows(UserNotFoundException.class, () -> userController.update(user, USER_EMAIL));
//            verify(userService, times(1)).update(user, USER_EMAIL);
//        }
//
//        @Test
//        void testUpdateUserShouldThrowIfEmailAlreadyExists() throws UserNotFoundException, EmailExistException {
//            when(userService.update(user, USER_EMAIL)).thenThrow(emailExistException);
//            assertThrows(EmailExistException.class, () -> userController.update(user, USER_EMAIL));
//            verify(userService, times(1)).update(user, USER_EMAIL);
//        }
//    }

    @Nested
    @DisplayName("Delete User")
    class DeleteUser {

        @Test
        @DisplayName("Should delete user when user has DELETE authority")
        @WithMockUser(username = "leeroy@jenkins", authorities = {"DELETE"})
        void testDeleteUser() throws Exception {
            doNothing().when(userService).delete(any());
            mockMvc.perform(delete("/api/v1/user/delete/{id}", 1L)
                            .with(user("asd").password("asd").authorities(new SimpleGrantedAuthority("DELETE")))
                    )
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should throw 401-UNAUTHORIZED AccessDeniedException when user does not have DELETE authority")
        @WithMockUser(username = "leeroy@jenkins", password = "123456", authorities = {"UPDATE"})
        void testDeleteUserWithoutDeleteAuthority() throws Exception {
            doNothing().when(userService).delete(1L);
            mockMvc.perform(delete("http://localhost:8080/api/v1/user/delete/{id}", 1L)
            ).andExpect(status().isUnauthorized());

            verify(userService, times(0)).delete(1L);
        }

    }


}