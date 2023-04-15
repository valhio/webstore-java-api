package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.domain.HttpResponse;
import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.ExceptionHandling;
import com.github.valhio.storeapi.exception.domain.EmailExistException;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static com.github.valhio.storeapi.constant.SecurityConstant.JWT_TOKEN_HEADER;

/*
    The UserController class is a Spring Boot REST controller that provides API endpoints for user-related operations
    such as registration, login, user retrieval, update, and deletion.
    The controller uses Spring Security to manage user authentication and authorization.
    The class extends the ExceptionHandling class, which provides global exception handling for the controller.
*/

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

    @GetMapping("/greeting")
    public String greeting(@AuthenticationPrincipal UserPrincipal userPrincipal) {
//    public String greeting(@AuthenticationPrincipal(expression = "username") String username) {
        return "Hello " + userPrincipal.getEmail();
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
    public ResponseEntity<User> login(@RequestBody User user) throws UserNotFoundException {
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

    @GetMapping("/{userId}")
    @PreAuthorize("!hasAnyRole('ROLE_GUEST')")
    public ResponseEntity<HttpResponse> getUserByUserId(@PathVariable("userId") String userId) throws UserNotFoundException {
        User user = userService.findUserByUserId(userId);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("user", user))
                .message("User retrieved successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }


    @GetMapping("/email/{email}")
    @PreAuthorize("!hasAnyRole('ROLE_GUEST')")
    public ResponseEntity<HttpResponse> findUserByEmail(@PathVariable("email") String email) throws UserNotFoundException {
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
    @PreAuthorize("hasAnyRole('ROLE_HR','ROLE_MANAGER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
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
    @PreAuthorize("!hasRole('ROLE_GUEST')")
    public ResponseEntity<HttpResponse> update(@RequestBody User user, @PathVariable String originalEmail) throws EmailExistException, UserNotFoundException {
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

    @PutMapping("/{userId}/first-name")
    @PreAuthorize("!hasRole('ROLE_GUEST')")
    public ResponseEntity<HttpResponse> updateFirstName(@PathVariable("userId") String userId, @RequestBody String firstName) throws UserNotFoundException {
        User updated = userService.updateFirstName(userId, firstName);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("user", updated))
                .message("User updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PutMapping("/{userId}/last-name")
    @PreAuthorize("!hasRole('ROLE_GUEST')")
    public ResponseEntity<HttpResponse> updateLastName(@PathVariable("userId") String userId, @RequestBody String lastName) throws UserNotFoundException {
        User updated = userService.updateLastName(userId, lastName);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("user", updated))
                .message("User updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PutMapping("/{userId}/phone-number")
    @PreAuthorize("!hasRole('ROLE_GUEST')")
    public ResponseEntity<HttpResponse> updatePhoneNumber(@PathVariable("userId") String userId, @RequestBody String phoneNumber) throws UserNotFoundException {
        User updated = userService.updatePhoneNumber(userId, phoneNumber);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("user", updated))
                .message("User updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PutMapping("/{userId}/address")
    @PreAuthorize("!hasRole('ROLE_GUEST')")
    public ResponseEntity<HttpResponse> updateAddress(@PathVariable("userId") String userId, @RequestBody String address) throws UserNotFoundException {
        User updated = userService.updateAddress(userId, address);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("user", updated))
                .message("User updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE')")
    public ResponseEntity<HttpResponse> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .message("User deleted successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    // TODO: Implement valid Reset Password Functionality
    @PostMapping("/reset-password/{email}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
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
    @PreAuthorize("#userPrincipal.email == #email or hasAnyRole('ROLE_MANAGER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<HttpResponse> updatePassword(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                       @RequestParam @NotBlank String email,
                                                       @RequestParam @NotBlank String currentPassword,
                                                       @RequestParam @NotBlank String newPassword) throws PasswordNotMatchException, UserNotFoundException {
        userService.updatePassword(email, currentPassword, newPassword);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .message("Password updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PutMapping("/update-email")
    @PreAuthorize("#userPrincipal.email == #email or hasAnyRole('ROLE_MANAGER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<HttpResponse> updateEmail(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                    @RequestParam @NotBlank String email,
                                                    @RequestParam @NotBlank String newEmail) throws EmailExistException, UserNotFoundException {
        userService.updateEmail(email, newEmail);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .message("Email updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/{email}/role")
    @PreAuthorize("#userPrincipal.email == #email or hasAnyRole('ROLE_MANAGER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<HttpResponse> getUserRole(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("email") String email) throws UserNotFoundException {
        Role role = userService.getUserRole(email);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("role", role))
                .message("User role retrieved successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/{email}/authorities")
    @PreAuthorize("#userPrincipal.email == #email or hasAnyRole('ROLE_MANAGER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<HttpResponse> getUserAuthority(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("email") String email) throws UserNotFoundException {
        Collection<String> authorities = userService.getUserAuthorities(email);
        return ResponseEntity.ok(HttpResponse.builder()
                .timeStamp(new Date())
                .data(Map.of("authorities", authorities))
                .message("User authorities retrieved successfully")
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
