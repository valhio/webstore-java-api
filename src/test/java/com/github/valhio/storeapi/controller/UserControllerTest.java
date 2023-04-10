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
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.service.UserService;
import com.github.valhio.storeapi.utility.JWTTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({SecurityConfiguration.class, JWTAuthorizationFilter.class, JWTAccessDeniedHandler.class, JWTAuthenticationEntryPoint.class})
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc()
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

    @Nested
    @DisplayName("Register User")
    class RegisterUser {

        @Test
        @WithMockUser(username = "leeroy@jenkins", authorities = {"READ"})
        void testRegisterUser() throws Exception {
            when(userService.register(any())).thenReturn(user);
            String json = objectMapper.writeValueAsString(user);
            mockMvc.perform(post("/api/v1/user/register").content(json).contentType("application/json"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.user.email", is(user.getEmail())))
                    .andExpect(jsonPath("$.data.user.firstName", is(user.getFirstName())))
                    .andExpect(jsonPath("$.data.user.lastName", is(user.getLastName())))
                    .andExpect(jsonPath("$.data.user.role", is(user.getRole().name())))
                    .andExpect(jsonPath("$.data.user.authorities", containsInAnyOrder("READ")));
            verify(userService, times(1)).register(any());
        }

        @Test
        void testRegisterUserShouldThrowIfEmailAlreadyExists() throws Exception {
            when(userService.register(any())).thenThrow(emailExistException);
            String json = objectMapper.writeValueAsString(user);

            mockMvc.perform(post("/api/v1/user/register").content(json).contentType("application/json"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                    .andExpect(jsonPath("$.message", is("Email already exists")));

            verify(userService, times(1)).register(any());
        }

    }

    @Nested
    @DisplayName("Login User")
    class LoginUser {

        @Test
        void testLoginUser() throws Exception {
            UserPrincipal userPrincipal = new UserPrincipal(user);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Jwt-Token", "test-jwt-token");
            when(userService.findUserByEmail(USER_EMAIL)).thenReturn(user);
            when(authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(USER_EMAIL, USER_PASSWORD)))
                    .thenReturn(new UsernamePasswordAuthenticationToken(userPrincipal, null, Collections.emptyList()));
            when(jwtTokenProvider.generateJwtToken(ArgumentMatchers.any())).thenReturn("test-jwt-token");


            mockMvc.perform(post("/api/v1/user/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isOk())
                    .andExpect(header().stringValues("Jwt-Token", "test-jwt-token"))
                    .andExpect(jsonPath("$.email", is(user.getEmail())))
                    .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                    .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                    .andExpect(jsonPath("$.role", is(user.getRole().name())))
                    .andExpect(jsonPath("$.authorities", containsInAnyOrder("READ")))
                    .andReturn()
                    .getResponse();

            verify(userService, times(1)).findUserByEmail(USER_EMAIL);
            verify(authenticationManager, times(1)).authenticate(
                    new UsernamePasswordAuthenticationToken(USER_EMAIL, USER_PASSWORD));
            verify(jwtTokenProvider, times(1)).generateJwtToken(ArgumentMatchers.any());
        }

        @Test
        void testLoginUserShouldThrowIfUserNotFound() throws Exception {
            when(userService.findUserByEmail(any())).thenThrow(userNotFoundException);

            mockMvc.perform(post("/api/v1/user/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                    .andExpect(jsonPath("$.message", is("User not found")));

            verify(userService, times(1)).findUserByEmail(any());
        }
    }

//    @Nested
//    @DisplayName("Is JWT Token Valid")
//    class isJwtValid {
//
//        @Test
//        @WithMockUser(authorities = {"READ"}, username = "leeroy@jenkins", value = "leeroy@jenkins")
//        void testIsJwtValid() throws Exception {
////            HttpServletRequest request = mock(HttpServletRequest.class);
////            when(request.getHeader("Authorization")).thenReturn("Bearer test-jwt-token");
////            when(request.getHeader("Email")).thenReturn(USER_EMAIL);
//            when(jwtTokenProvider.isTokenValid(any(), any())).thenReturn(true);
//
////            boolean jwtValid = userController.isJwtValid(request)
//
//            // Set Authorization header
//            MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
//            mockHttpServletRequest.addHeader("Authorization","Bearer test-jwt-token");
//
//            mockMvc.perform(get("/api/v1/user/isJwtValid")
//                            .with(request -> {
//                                request.addHeader("Authorization", "Bearer test-jwt-token");
//                                return request;
//                            })
//                            .contentType(MediaType.APPLICATION_JSON)                        )
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.data", is(true)));
//
//        }
//    }

    @Nested
    @DisplayName("Find User By Email")
    class FindUserByEmail {

        @Test
        @WithMockUser(authorities = {"READ"}, username = "leeroy@jenkins")
        void testFindUserByEmail() throws Exception {
            when(userService.findUserByEmail(any())).thenReturn(user);

            mockMvc.perform(get("/api/v1/user/email/" + USER_EMAIL)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.user.email", is(user.getEmail())))
                    .andExpect(jsonPath("$.data.user.firstName", is(user.getFirstName())))
                    .andExpect(jsonPath("$.data.user.lastName", is(user.getLastName())))
                    .andExpect(jsonPath("$.data.user.role", is(user.getRole().name())))
                    .andExpect(jsonPath("$.data.user.authorities", containsInAnyOrder("READ")));

            verify(userService, times(1)).findUserByEmail(USER_EMAIL);
        }

        @Test
        @WithMockUser(authorities = {"READ"}, username = "leeroy@jenkins")
        void testFindUserByEmailShouldThrowIfUserNotFound() throws Exception {
            when(userService.findUserByEmail(any())).thenThrow(userNotFoundException);

            mockMvc.perform(get("/api/v1/user/email/" + USER_EMAIL)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                    .andExpect(jsonPath("$.message", is("User not found")));

            verify(userService, times(1)).findUserByEmail(USER_EMAIL);
        }
    }

    @Nested
    @DisplayName("Fetch all users")
    class FetchAllUsers {

        @Test
        @WithMockUser(authorities = {"READ", "ROLE_HR"}, username = "leeroy@jenkins", value = "leeroy@jenkins")
        void testFetchAllUsersShouldPassWithHRRole() throws Exception {
            Page<User> page = new PageImpl<>(List.of(user, new User()), PageRequest.of(0, 10), 1);
            when(userService.getUsers("", 0, 10)).thenReturn(page);

            mockMvc.perform(get("/api/v1/user/list")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.usersPage.totalElements", is(2)))
                    .andExpect(jsonPath("$.data.usersPage.content[0].email", is(user.getEmail())))
                    .andExpect(jsonPath("$.data.usersPage.content[0].firstName", is(user.getFirstName())))
                    .andExpect(jsonPath("$.data.usersPage.content[0].lastName", is(user.getLastName())))
                    .andExpect(jsonPath("$.data.usersPage.content[0].role", is(user.getRole().name())))
                    .andExpect(jsonPath("$.data.usersPage.content[0].authorities", containsInAnyOrder("READ")));

            verify(userService, times(1)).getUsers("", 0, 10);
        }

        @Test
        @WithMockUser(roles = {"MANAGER"}, username = "leeroy@jenkins", value = "leeroy@jenkins")
        void testFetchAllUsersShouldPassWithManagerRole() throws Exception {
            Page<User> page = new PageImpl<>(List.of(user, new User()), PageRequest.of(0, 10), 1);
            when(userService.getUsers("", 0, 10)).thenReturn(page);

            mockMvc.perform(get("/api/v1/user/list")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.usersPage.totalElements", is(2)));

            verify(userService, times(1)).getUsers("", 0, 10);
        }

        @Test
        @WithMockUser(roles = {"ADMIN"}, username = "leeroy@jenkins", value = "leeroy@jenkins")
        void testFetchAllUsersShouldPassWithAdminRole() throws Exception {
            Page<User> page = new PageImpl<>(List.of(user, new User()), PageRequest.of(0, 10), 1);
            when(userService.getUsers("", 0, 10)).thenReturn(page);

            mockMvc.perform(get("/api/v1/user/list")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.usersPage.totalElements", is(2)));

            verify(userService, times(1)).getUsers("", 0, 10);
        }

        @Test
        @WithMockUser(roles = {"SUPER_ADMIN"}, username = "leeroy@jenkins", value = "leeroy@jenkins")
        void testFetchAllUsersShouldPassWithSuperAdminRole() throws Exception {
            Page<User> page = new PageImpl<>(List.of(user, new User()), PageRequest.of(0, 10), 1);
            when(userService.getUsers("", 0, 10)).thenReturn(page);

            mockMvc.perform(get("/api/v1/user/list")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.usersPage.totalElements", is(2)));

            verify(userService, times(1)).getUsers("", 0, 10);
        }

        @Test
        @WithMockUser(roles = {"USER"}, username = "leeroy@jenkins", value = "leeroy@jenkins")
        void testFetchAllUsersShouldThrow401UnauthorizedWithUserRole() throws Exception {
            mockMvc.perform(get("/api/v1/user/list")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isUnauthorized());

            verify(userService, times(0)).getUsers("", 0, 10);
        }

        @Test
        @WithMockUser(roles = {"GUEST"}, username = "leeroy@jenkins", value = "leeroy@jenkins")
        void testFetchAllUsersShouldThrow401UnauthorizedWithGuestRole() throws Exception {
            mockMvc.perform(get("/api/v1/user/list").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
            verify(userService, times(0)).getUsers("", 0, 10);
        }

        @Test
        void testFetchAllUsersShouldThrow403ForbiddenWhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/v1/user/list").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
            verify(userService, times(0)).getUsers("", 0, 10);
        }
    }

    @Nested
    @DisplayName("Update User")
    class UpdateUser {

        @Test
        @WithMockUser(authorities = {"UPDATE", "ROLE_MANAGER"}, username = "leeroy@jenkins", value = "leeroy@jenkins")
        void testUpdateUserShouldPassWithRoleManagerAndAuthorityUpdate() throws Exception {
            User newUser = new User();
            when(userService.update(any(), any())).thenReturn(UserControllerTest.this.user);

            mockMvc.perform(post("/api/v1/user/update/" + USER_EMAIL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUser))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.user.email", is(user.getEmail())))
                    .andExpect(jsonPath("$.data.user.firstName", is(user.getFirstName())))
                    .andExpect(jsonPath("$.data.user.lastName", is(user.getLastName())))
                    .andExpect(jsonPath("$.data.user.role", is(user.getRole().name())))
                    .andExpect(jsonPath("$.data.user.authorities", containsInAnyOrder("READ")));

            verify(userService, times(1)).update(any(), any());
        }

        @Test
        @WithMockUser(authorities = {"ROLE_GUEST"}, username = "leeroy@jenkins", value = "leeroy@jenkins")
        void testUpdateUserShouldThrow401IfUserHasRoleGuest() throws Exception {
            mockMvc.perform(post("/api/v1/user/update/" + USER_EMAIL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new User()))
                    )
                    .andExpect(status().isUnauthorized());
            verify(userService, times(0)).update(any(), any());
        }


        @Test
        @WithMockUser(authorities = {"UPDATE", "ROLE_MANAGER"}, username = "leeroy@jenkins", value = "leeroy@jenkins")
        void testUpdateUserShouldThrowIfUserNotFound() throws Exception {
            when(userService.update(any(), any())).thenThrow(userNotFoundException);

            mockMvc.perform(post("/api/v1/user/update/" + USER_EMAIL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user))
                    )
                    .andExpect(status().isNotFound());

            verify(userService, times(1)).update(any(), any());
        }

        @Test
        @WithMockUser(authorities = {"UPDATE", "ROLE_MANAGER"}, username = "leeroy@jenkins", value = "leeroy@jenkins")
        void testUpdateUserShouldThrowIfEmailAlreadyExists() throws Exception {
            when(userService.update(any(), any())).thenThrow(emailExistException);

            mockMvc.perform(post("/api/v1/user/update/" + USER_EMAIL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user))
                    )
                    .andExpect(status().isBadRequest());

            verify(userService, times(1)).update(any(), any());
        }
    }

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