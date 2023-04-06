package com.github.valhio.storeapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valhio.storeapi.domain.HttpResponse;
import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.ExceptionHandling;
import com.github.valhio.storeapi.exception.domain.EmailExistException;
import com.github.valhio.storeapi.exception.domain.EmailNotFoundException;
import com.github.valhio.storeapi.exception.domain.PasswordNotMatchException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.service.UserService;
import com.github.valhio.storeapi.utility.JWTTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.*;

import static com.github.valhio.storeapi.constant.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(path = {"/api/v1/user"})
public class UserController extends ExceptionHandling {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        //Invalidate JWT token
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            // Does not work, no need for implementation
            jwtTokenProvider.invalidateToken(token);
        }
        return ResponseEntity.ok("Successfully logged out");
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@RequestBody User user) throws EmailExistException, IllegalArgumentException {
        User registered = userService.register(user);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("user", registered))
                .message("User registered successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    // return response entity with jwt header
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getEmail(), user.getPassword());
        User logged = userService.findUserByEmail(user.getEmail());
        UserPrincipal userPrincipal = new UserPrincipal(logged);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return ResponseEntity.ok()
                .headers(jwtHeader)
                .body(logged);
    }

    @GetMapping("/isJwtValid")
    public boolean isJwtValid(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtTokenProvider.isTokenValid(request.getHeader("Email"), token);
    }

    @PostMapping(path = "/add")
    public ResponseEntity<HttpResponse> addNewUser(@RequestParam String user) throws EmailExistException, IllegalArgumentException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        User newUser = objectMapper.readValue(user, User.class);
        User registered = userService.addNewUser(newUser);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("user", registered))
                .message("User added successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<HttpResponse> findUserByEmail(@PathVariable("email") String email) {
        User user = userService.findUserByEmail(email);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("user", user))
                .message("User retrieved successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/list")
    public ResponseEntity<HttpResponse> getUsers(@RequestParam Optional<String> keyword,
                                                 @RequestParam Optional<Integer> page,
                                                 @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("usersPage", userService.getUsers(keyword.orElse(""), page.orElse(0), size.orElse(10))))
                .message("Users retrieved successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PostMapping("/update/{originalEmail}")
    @PreAuthorize("hasAnyAuthority('UPDATE')")
    public ResponseEntity<HttpResponse> update(@RequestBody User user, @PathVariable String originalEmail) throws EmailExistException, IOException, EmailNotFoundException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        User newUser = objectMapper.readValue(user, User.class);
        User updated = userService.update(user, originalEmail);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("user", updated))
                .message("User updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('DELETE')")
    public ResponseEntity<HttpResponse> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .message("User deleted successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @DeleteMapping("/delete/userId/{userId}")
    @PreAuthorize("hasAnyAuthority('DELETE')")
    public ResponseEntity<HttpResponse> deleteByUserId(@PathVariable("userId") String userId) {
        userService.deleteByUserId(userId);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .message("User deleted successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    // TODO: Implement valid Reset Password Functionality
    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws UserNotFoundException {
        userService.resetPassword(email);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("email", email))
                .message("Password reset successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PostMapping("/update-password")
    public ResponseEntity<HttpResponse> updatePassword(@RequestParam @NotBlank String username,
                                                       @RequestParam @NotBlank String currentPassword,
                                                       @RequestParam @NotBlank String newPassword) throws PasswordNotMatchException, UserNotFoundException {
        userService.updatePassword(username, currentPassword, newPassword);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .message("Password updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PostMapping("/update-email")
    public ResponseEntity<HttpResponse> updateEmail(@RequestParam @NotBlank String username,
                                                    @RequestParam @NotBlank String currentPassword,
                                                    @RequestParam @NotBlank String newEmail) throws EmailExistException, PasswordNotMatchException {
        userService.updateEmail(username, currentPassword, newEmail);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .message("Email updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/{username}/role")
    public ResponseEntity<HttpResponse> getUserRole(@PathVariable("username") String username) {
        Role role = userService.getUserRole(username);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("role", role))
                .message("User role retrieved successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/{username}/authority")
    public ResponseEntity<HttpResponse> getUserAuthority(@PathVariable("username") String username) {
        Set<String> authorities = userService.getUserAuthorities(username);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("authorities", authorities))
                .message("User authorities retrieved successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/list/role")
    public ResponseEntity<HttpResponse> getAllUsersByRole(@RequestParam String role) {
        Collection<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("users", users))
                .message("Users retrieved successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    //    @PostMapping("/add-role")
//    public ResponseEntity<HttpResponse> addRoleToUser(@RequestBody Map<String, Object> roleData) {
//        userService.addRoleToUser(roleData);
//        return ResponseEntity.ok(HttpResponse.builder()
//                .timeStamp(new Date())
//                .data(Map.of("roleData", roleData))
//                .message("Role added successfully")
//                .status(HttpStatus.OK)
//                .statusCode(HttpStatus.OK.value())
//                .build());
//    }
//
//    @PostMapping("/remove-role")
//    public ResponseEntity<HttpResponse> removeRoleFromUser(@RequestBody Map<String, Object> roleData) {
//        userService.removeRoleFromUser(roleData);
//        return ResponseEntity.ok(HttpResponse.builder()
//                .timeStamp(new Date())
//                .data(Map.of("roleData", roleData))
//                .message("Role removed successfully")
//                .status(HttpStatus.OK)
//                .statusCode(HttpStatus.OK.value())
//                .build());
//    }

    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    // Throws LockedException, DisabledException, BadCredentialsException, AccountExpiredException, CredentialsExpiredException ...
    // Locked exception is thrown if the UserPrincipal's isAccountNonLocked() method returns false.
    private void authenticate(String email, String password) {
        // Calls UserDetailsService.loadUserByEmail() to get the user
        // Then calls AuthenticationManager.authenticate() to authenticate the user
        // If the user is not authenticated, an exception is thrown and caught by the controller.
        // If the user is authenticated, the method returns and the user is logged in.
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
}
