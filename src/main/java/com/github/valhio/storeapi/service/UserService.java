package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.domain.EmailExistException;
import com.github.valhio.storeapi.exception.domain.PasswordNotMatchException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.User;
import org.springframework.data.domain.Page;

import java.util.Collection;

public interface UserService {
    User register(User user) throws EmailExistException, IllegalArgumentException;

    User findUserByEmail(String email) throws UserNotFoundException;

    User findUserByUserId(String id) throws UserNotFoundException;

    Page<User> getUsers(String keyword, int page, int size);

    User update(User newUser, String originalEmail) throws EmailExistException, UserNotFoundException;

    void delete(Long id);

    void deleteByUserId(String userId);

    void resetPassword(String email) throws UserNotFoundException;

    User updatePassword(String username, String currentPassword, String newPassword) throws PasswordNotMatchException, UserNotFoundException;

    Role getUserRole(String username) throws UserNotFoundException;

    Collection<User> getUsersByRole(String role) throws UserNotFoundException;

    Collection<String> getUserAuthorities(String username) throws UserNotFoundException;

    void validateEmailExists(String email) throws EmailExistException;

    User updateFirstName(String userId, String firstName) throws UserNotFoundException;

    User updateLastName(String userId, String lastName) throws UserNotFoundException;

    User updateEmail(String username, String newEmail) throws EmailExistException, UserNotFoundException;

    User updatePhoneNumber(String userId, String phoneNumber) throws UserNotFoundException;

    User updateAddress(String userId, String address) throws UserNotFoundException;

//    void AddRoleToUser(String username, String role);
//
//    void removeRoleFromUser(String username, String role);
}

