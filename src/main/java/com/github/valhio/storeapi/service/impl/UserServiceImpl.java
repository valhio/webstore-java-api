package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.domain.EmailExistException;
import com.github.valhio.storeapi.exception.domain.PasswordNotMatchException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.repository.UserRepository;
import com.github.valhio.storeapi.service.LoginAttemptService;
import com.github.valhio.storeapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.valhio.storeapi.constant.UserImplConstant.*;
import static com.github.valhio.storeapi.enumeration.Role.ROLE_SUPER_ADMIN;
import static com.github.valhio.storeapi.enumeration.Role.ROLE_USER;

@Slf4j
@Transactional
@Qualifier("userDetailsService")
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    // This method is called by Spring Security when a user tries to log in.
    // This method is used by Spring Security to load a user by username (in this case, the email).
    // It is used during the authentication process inside the AuthenticationManager located in the UserController class.
    @Override
    public UserDetails loadUserByUsername(String email) {
        // Cannot use the method findUserByEmail() because it throws a UserNotFoundException but loadUserByUsername() can only throw a UsernameNotFoundException.
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        validateLoginAttempt(user);
        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        log.info("User found by email: " + email);
        return userPrincipal;
    }

    // Validate if the user is locked or not (if the user has exceeded the maximum number of login attempts).
    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.isBlocked(user.getEmail()));
        } else {
            loginAttemptService.loginSucceeded(user.getEmail());
        }
    }

    @Override
    public User register(User user) throws EmailExistException, IllegalArgumentException {
        validateEmailExists(user.getEmail());
        user.setUserId(UUID.randomUUID().toString().concat("-" + LocalDateTime.now().getNano()));
//        user.setUserId(UUID.randomUUID().toString().concat("-" + encodeUsername(user.getUsername())));
        user.setPassword(encodePassword(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Objects.equals(user.getEmail(), "a@a.com") ? ROLE_SUPER_ADMIN : ROLE_USER);
        user.setAuthorities(user.getRole().getAuthorities());
        return userRepository.save(user);
    }

    @Override
    public Page<User> getUsers(String keyword, int page, int size) {
        return userRepository.findAll(keyword, PageRequest.of(page, size));
    }

    @Override
    public User update(User newUser, String originalEmail) throws EmailExistException, UserNotFoundException {
        User userByEmail = this.findUserByEmail(originalEmail);

        if (!userByEmail.getEmail().equals(newUser.getEmail())) validateEmailExists(newUser.getEmail());

        // Map the new user to the old user.
        userByEmail.setFirstName(newUser.getFirstName() == null ? userByEmail.getFirstName() : newUser.getFirstName());
        userByEmail.setLastName(newUser.getLastName() == null ? userByEmail.getLastName() : newUser.getLastName());
        userByEmail.setEmail(newUser.getEmail() == null ? userByEmail.getEmail() : newUser.getEmail());
        userByEmail.setPhone(newUser.getPhone() == null ? userByEmail.getPhone() : newUser.getPhone());
        userByEmail.setAddress(newUser.getAddress() == null ? userByEmail.getAddress() : newUser.getAddress());
        userByEmail.setRole(newUser.getRole() == null ? userByEmail.getRole() : newUser.getRole());
        userByEmail.setAuthorities(newUser.getRole().getAuthorities() == null ? userByEmail.getAuthorities() : newUser.getRole().getAuthorities());
        userByEmail.setActive(newUser.isActive());
        userByEmail.setNotLocked(newUser.isNotLocked());
        userByEmail.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(userByEmail);
    }

    @Override
    public void delete(Long id) {
        userRepository.findById(id).ifPresent(userRepository::delete);
    }

    @Override
    public void deleteByUserId(String userId) {
        userRepository.findByUserId(userId).ifPresent(userRepository::delete);
    }

    // TODO: Implement valid Reset Password Functionality
    @Override
    public void resetPassword(String email) throws UserNotFoundException {
        User user = this.findUserByEmail(email);
        if (user == null) throw new UserNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        user.setPassword(encodePassword("password"));
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_EMAIL + email));
    }

    @Override
    public User findUserByUserId(String userId) throws UserNotFoundException {
        return userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_USER_ID + userId));
    }

    @Override
    public User updatePassword(String email, String currentPassword, String newPassword) throws PasswordNotMatchException, UserNotFoundException {
        User user = this.findUserByEmail(email);
        if (!passwordEncoder.matches(currentPassword, user.getPassword()))
            throw new PasswordNotMatchException(INCORRECT_CURRENT_PASSWORD);
        user.setPassword(encodePassword(newPassword));
        return userRepository.save(user);
    }

    @Override
    public User updateEmail(String email, String newEmail) throws EmailExistException, UserNotFoundException {
        User user = this.findUserByEmail(email);
        validateEmailExists(newEmail);
        user.setEmail(newEmail);
        return userRepository.save(user);
    }

    @Override
    public Role getUserRole(String email) throws UserNotFoundException {
        User user = this.findUserByEmail(email);
        return user.getRole();
    }

    @Override
    public Collection<String> getUserAuthorities(String email) throws UserNotFoundException {
        User user = this.findUserByEmail(email);
        return Arrays.stream(user.getAuthorities()).collect(Collectors.toList());
    }

    @Override
    public Collection<User> getUsersByRole(String role) throws UserNotFoundException {
        return userRepository.findAllByRole(Role.valueOf(role.toUpperCase()))
                .orElseThrow(() -> new UserNotFoundException("No users found with role: " + role));
    }

    @Override
    public void validateEmailExists(String email) throws EmailExistException, IllegalArgumentException {
        validateString(email);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            throw new EmailExistException(EMAIL_ALREADY_EXISTS);
        }
    }

    @Override
    public User updateFirstName(String userId, String firstName) throws UserNotFoundException {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_USER_ID + userId));
        user.setFirstName(firstName);
        return userRepository.save(user);
    }

    @Override
    public User updateLastName(String userId, String lastName) throws UserNotFoundException {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_USER_ID + userId));
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    @Override
    public User updatePhoneNumber(String userId, String phone) throws UserNotFoundException {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_USER_ID + userId));
        user.setPhone(phone);
        return userRepository.save(user);
    }

    @Override
    public User updateAddress(String userId, String address) throws UserNotFoundException {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_USER_ID + userId));
        user.setAddress(address);
        return userRepository.save(user);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void validateString(String argument) throws IllegalArgumentException {
        if (argument == null || argument.isEmpty()) {
            throw new IllegalArgumentException("Argument cannot be null or empty");
        }
    }

}
