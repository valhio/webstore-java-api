package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.domain.EmailExistException;
import com.github.valhio.storeapi.exception.domain.PasswordNotMatchException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.User;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    User register(User user) throws EmailExistException, IllegalArgumentException;

    User findUserByEmail(String email) throws UserNotFoundException;

    Optional<User> findUserByUserId(String id);

    Page<User> getUsers(String keyword, int page, int size);

    User update(User newUser, String originalEmail) throws EmailExistException, UserNotFoundException;

    void delete(Long id);

    void deleteByUserId(String userId);

    void resetPassword(String email) throws UserNotFoundException;

    void updatePassword(String username, String currentPassword, String newPassword) throws PasswordNotMatchException, UserNotFoundException;

    void updateEmail(String username, String currentPassword, String newEmail) throws PasswordNotMatchException, EmailExistException, UserNotFoundException;

    Role getUserRole(String username) throws UserNotFoundException;

    Collection<User> getUsersByRole(String role);

    Set<String> getUserAuthorities(String username) throws UserNotFoundException;

    void validateEmailExists(String email) throws EmailExistException;

//    void AddRoleToUser(String username, String role);
//
//    void removeRoleFromUser(String username, String role);
}

