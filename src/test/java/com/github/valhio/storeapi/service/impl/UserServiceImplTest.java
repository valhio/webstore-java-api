package com.github.valhio.storeapi.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @Mock
//    private LoginAttemptService loginAttemptService;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    @Test
//    @DisplayName("Test get users")
//    void testGetUsers() {
//        String keyword = "test";
//        int page = 0;
//        int size = 10;
//        PageRequest pageRequest = PageRequest.of(page, size);
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("testuser");
//        Page<User> pageResult = new PageImpl<>(Collections.singletonList(user));
//        when(userRepository.findAll(keyword, pageRequest)).thenReturn(pageResult);
//
//        Page<User> result = userService.getUsers(keyword, page, size);
//
//        Assertions.assertEquals(pageResult, result);
//        verify(userRepository).findAll(keyword, pageRequest);
//    }
//
//    @Nested
//    @DisplayName("Test load user by username")
//    class TestLoadUserByUsername {
//
//        @Test
//        void testLoadUserByUsername() {
//            String email = "john.doe@example.com";
//            User user = new User();
//            user.setId(1L);
//            user.setEmail(email);
//            user.setPassword("password");
//            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//            when(userRepository.save(user)).thenReturn(user);
//
//            UserDetails userDetails = userService.loadUserByUsername(email);
//
//            assertNotNull(userDetails);
//            assertEquals(user.getEmail(), userDetails.getUsername());
//            assertEquals(user.getPassword(), userDetails.getPassword());
//            verify(userRepository, times(1)).save(user);
//            verify(userRepository, times(1)).findByEmail(email);
//        }
//
//        @Test
//        void testLoadUserByUsernameWithInvalidEmail() {
//            String email = "john.doe@example.com";
//
//            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//            assertThrows(UsernameNotFoundException.class, () -> {
//                userService.loadUserByUsername(email);
//            });
//        }
//    }
//
//    @Nested
//    @DisplayName("Test register")
//    class TestRegister {
//
//        @Test
//        @DisplayName("Test register should succeed")
//        void testRegister() throws EmailExistException {
//            User user = new User();
//            user.setId(1L);
//            user.setEmail("leeroy");
//            user.setPassword("password");
//
//            when(userRepository.existsByEmail(anyString())).thenReturn(false);
//            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//            when(userRepository.save(any(User.class))).thenReturn(user);
//
//            User res = userService.register(user);
//
//            assertEquals(user, res);
//            verify(userRepository, times(1)).existsByEmail(anyString());
//            verify(passwordEncoder, times(1)).encode(anyString());
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test register should throw EmailExistException")
//        void testRegisterWithExistingEmail() {
//            User user = new User();
//            user.setId(1L);
//            user.setEmail("leeroy");
//            user.setPassword("password");
//
//            when(userRepository.existsByEmail(anyString())).thenReturn(true);
//
//            assertThrows(EmailExistException.class, () -> {
//                userService.register(user);
//            });
//
//            verify(userRepository, times(1)).existsByEmail(anyString());
//            verify(passwordEncoder, never()).encode(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test register should throw IllegalArgumentException if no email is passed")
//        void testRegisterWithInvalidEmail() {
//            User user = new User();
//            user.setId(1L);
//            user.setEmail("");
//            user.setPassword("password");
//
//            assertThrows(IllegalArgumentException.class, () -> {
//                userService.register(user);
//            });
//
//            verify(userRepository, never()).existsByEmail(anyString());
//            verify(passwordEncoder, never()).encode(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Test update user")
//    class UpdateUser {
//        @Test
//        @DisplayName("Test update user should succeed when email is not changed")
//        void testUpdateUser() throws EmailExistException, UserNotFoundException {
//            User newUser = new User();
//            newUser.setId(1L);
//            newUser.setEmail("NOT leeroy");
//            newUser.setPassword("password");
//            newUser.setRole(Role.ROLE_USER);
//            newUser.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            User user = new User();
//            user.setId(1L);
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//            when(userRepository.save(any(User.class))).thenReturn(newUser);
//
//            User res = userService.update(newUser, user.getEmail());
//
//            assertEquals(newUser, res);
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test update user should throw UserExistsException when email is changed to an existing email")
//        void testUpdateUserShouldThrowWhenNewEmailAlreadyExists() throws EmailExistException, UserNotFoundException {
//            User newUser = new User();
//            newUser.setId(1L);
//            newUser.setEmail("NOT leeroy");
//            newUser.setPassword("password");
//            newUser.setRole(Role.ROLE_USER);
//            newUser.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            User user = new User();
//            user.setId(1L);
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//            when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);
//
//            assertThrows(EmailExistException.class, () -> {
//                userService.update(newUser, user.getEmail());
//            });
//
//            verify(userRepository, times(1)).existsByEmail(anyString());
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test update user should throw UserNotFoundException when user is not found")
//        void testUpdateUserShouldThrowWhenUserNotFound() throws EmailExistException, UserNotFoundException {
//            User newUser = new User();
//            newUser.setId(1L);
//            newUser.setEmail("NOT leeroy");
//            newUser.setPassword("password");
//            newUser.setRole(Role.ROLE_USER);
//            newUser.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            User user = new User();
//            user.setId(1L);
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.update(newUser, user.getEmail());
//            });
//
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//
//    }
//
//    @Nested
//    @DisplayName("Test delete by Id")
//    class DeleteById {
//
//        @Test
//        @DisplayName("Test delete user")
//        void testDeleteUser() throws UserNotFoundException {
//            User user = new User();
//            user.setId(1L);
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findById(any())).thenReturn(Optional.of(user));
//            doNothing().when(userRepository).delete(any(User.class));
//
//            userService.delete(user.getId());
//
//            verify(userRepository, times(1)).findById(any());
//            verify(userRepository, times(1)).delete(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test delete should not delete if user does not exist")
//        void testDeleteUserShouldNotDeleteIfUserDoesNotExist() throws UserNotFoundException {
//            User user = new User();
//            user.setId(1L);
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findById(any())).thenReturn(Optional.empty());
//
//            userService.delete(user.getId());
//
//            verify(userRepository, times(1)).findById(any());
//            verify(userRepository, never()).delete(any(User.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Test delete by userId")
//    class DeleteByUserId {
//
//        @Test
//        @DisplayName("Test delete user by userId")
//        void testDeleteUser() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(any())).thenReturn(Optional.of(user));
//            doNothing().when(userRepository).delete(any(User.class));
//
//            userService.deleteByUserId(user.getUserId());
//
//            verify(userRepository, times(1)).findByUserId(any());
//            verify(userRepository, times(1)).delete(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test delete should not delete if user does not exist")
//        void testDeleteUserShouldNotDeleteIfUserDoesNotExist() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(any())).thenReturn(Optional.empty());
//
//            userService.deleteByUserId(user.getUserId());
//
//            verify(userRepository, times(1)).findByUserId(any());
//            verify(userRepository, never()).delete(any(User.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Test find user by email")
//    class FindByEmail {
//
//        @Test
//        @DisplayName("Test find user by email")
//        void testFindUserByEmail() throws UserNotFoundException {
//            User user = new User();
//            user.setId(1L);
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//
//            User res = userService.findUserByEmail(user.getEmail());
//
//            assertEquals(user, res);
//            verify(userRepository, times(1)).findByEmail(anyString());
//        }
//
//        @Test
//        @DisplayName("Test find user by email should throw UserNotFoundException when user is not found")
//        void testFindUserByEmailShouldThrowWhenUserNotFound() throws UserNotFoundException {
//            User user = new User();
//            user.setId(1L);
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.findUserByEmail(user.getEmail());
//            });
//
//            verify(userRepository, times(1)).findByEmail(anyString());
//        }
//    }
//
//    @Nested
//    @DisplayName("Test find user by userId")
//    class FindByUserId {
//
//        @Test
//        @DisplayName("Test find user by userId")
//        void testFindUserByUserId() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
//
//            User res = userService.findUserByUserId(user.getUserId());
//
//            assertEquals(user, res);
//            verify(userRepository, times(1)).findByUserId(anyString());
//        }
//
//        @Test
//        @DisplayName("Test find user by userId should throw UserNotFoundException when user is not found")
//        void testFindUserByUserIdShouldThrowWhenUserNotFound() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.findUserByUserId(user.getUserId());
//            });
//
//            verify(userRepository, times(1)).findByUserId(anyString());
//        }
//    }
//
//    @Nested
//    @DisplayName("Test update password")
//    class UpdatePassword {
//
//        @Test
//        @DisplayName("Should update password successfully")
//        void testUpdatePassword() throws UserNotFoundException, PasswordNotMatchException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
//            when(passwordEncoder.encode(anyString())).thenReturn("newPassword");
//            when(userRepository.save(any(User.class))).thenReturn(user);
//
//            User response = userService.updatePassword(user.getEmail(), user.getPassword(), "newPassword");
//
//
//            assertEquals(user, response);
//            assertEquals("newPassword", response.getPassword());
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(passwordEncoder, times(1)).matches(anyString(), anyString());
//            verify(passwordEncoder, times(1)).encode(anyString());
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test update password should throw UserNotFoundException when user is not found")
//        void testUpdatePasswordShouldThrowWhenUserNotFound() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.updatePassword(user.getUserId(), user.getPassword(), "newPassword");
//            });
//
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(passwordEncoder, never()).matches(anyString(), anyString());
//            verify(passwordEncoder, never()).encode(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test update password should throw PasswordNotMatchException when password does not match")
//        void testUpdatePasswordShouldThrowWhenPasswordDoesNotMatch() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
//
//            assertThrows(PasswordNotMatchException.class, () -> {
//                userService.updatePassword(user.getUserId(), user.getPassword(), "newPassword");
//            });
//
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(passwordEncoder, times(1)).matches(anyString(), anyString());
//            verify(passwordEncoder, never()).encode(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Test update email")
//    class UpdateEmail {
//
//        @Test
//        @DisplayName("Should update email successfully")
//        void testUpdateEmail() throws UserNotFoundException, EmailExistException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//            when(userRepository.existsByEmail(anyString())).thenReturn(false);
//            when(userRepository.save(any(User.class))).thenReturn(user);
//
//            User response = userService.updateEmail(user.getEmail(), "newEmail");
//
//            assertEquals(user, response);
//            assertEquals("newEmail", response.getEmail());
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test update email should throw UserNotFoundException when user is not found")
//        void testUpdateEmailShouldThrowWhenUserNotFound() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.updateEmail(user.getUserId(), "newEmail");
//            });
//
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test update email should throw EmailExistException when email already exist")
//        void testUpdateEmailShouldThrowWhenEmailExist() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//            when(userRepository.existsByEmail(anyString())).thenReturn(true);
//
//            assertThrows(EmailExistException.class, () -> {
//                userService.updateEmail(user.getUserId(), "newEmail");
//            });
//
//            verify(userRepository, times(1)).findByEmail(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Test get user role")
//    class GetUserRole {
//        @Test
//        @DisplayName("Should return user role successfully")
//        void testGetUserRole() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//
//            Role response = userService.getUserRole(user.getEmail());
//
//            assertEquals(user.getRole(), response);
//            verify(userRepository, times(1)).findByEmail(anyString());
//        }
//
//        @Test
//        @DisplayName("Test get user role should throw UserNotFoundException when user is not found")
//        void testGetUserRoleShouldThrowWhenUserNotFound() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.getUserRole(user.getEmail());
//            });
//
//            verify(userRepository, times(1)).findByEmail(anyString());
//        }
//    }
//
//    @Nested
//    @DisplayName("Test get user authorities")
//    class GetUserAuthorities {
//        @Test
//        @DisplayName("Should return user authorities successfully")
//        void testGetUserAuthorities() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//
//            String[] strings = userService.getUserAuthorities(user.getEmail()).toArray(new String[0]);
//
//            assertArrayEquals(user.getAuthorities(), strings);
//            verify(userRepository, times(1)).findByEmail(anyString());
//        }
//
//        @Test
//        @DisplayName("Test get user authorities should throw UserNotFoundException when user is not found")
//        void testGetUserAuthoritiesShouldThrowWhenUserNotFound() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.getUserAuthorities(user.getEmail());
//            });
//
//            verify(userRepository, times(1)).findByEmail(anyString());
//        }
//    }
//
//    @Nested
//    @DisplayName("Test get all users with role")
//    class GetAllUsersWithRole {
//        @Test
//        @DisplayName("Should return all users with role successfully")
//        void testGetAllUsersWithRole() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            User user2 = new User();
//            user2.setUserId("2");
//            user2.setEmail("leeroy2");
//            user2.setPassword("password");
//            user2.setRole(Role.ROLE_USER);
//            user2.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findAllByRole(any(Role.class))).thenReturn(Optional.of(List.of(user, user2)));
//
//            List<User> response = userService.getUsersByRole("ROLE_USER").stream().toList();
//
//            assertEquals(List.of(user, user2), response);
//            verify(userRepository, times(1)).findAllByRole(any(Role.class));
//        }
//
//        @Test
//        @DisplayName("Test get all users with role should throw UserNotFoundException when user is not found")
//        void testGetAllUsersWithRoleShouldThrowWhenUserNotFound() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            User user2 = new User();
//            user2.setUserId("2");
//            user2.setEmail("leeroy2");
//            user2.setPassword("password");
//            user2.setRole(Role.ROLE_USER);
//            user2.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findAllByRole(any(Role.class))).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.getUsersByRole("ROLE_USER");
//            });
//
//            verify(userRepository, times(1)).findAllByRole(any(Role.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Test update user's first name")
//    class UpdateFirstName {
//        @Test
//        @DisplayName("Should update user's first name successfully")
//        void testUpdateFirstName() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
//            when(userRepository.save(any(User.class))).thenReturn(user);
//
//            User response = userService.updateFirstName(user.getEmail(), "newFirstName");
//
//            assertEquals(user, response);
//            assertEquals("newFirstName", response.getFirstName());
//            verify(userRepository, times(1)).findByUserId(anyString());
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test update user's first name should throw UserNotFoundException when user is not found")
//        void testUpdateFirstNameShouldThrowWhenUserNotFound() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.updateFirstName(user.getEmail(), "newFirstName");
//            });
//
//            verify(userRepository, times(1)).findByUserId(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Test update user's last name")
//    class UpdateLastName {
//        @Test
//        @DisplayName("Should update user's last name successfully")
//        void testUpdateLastName() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
//            when(userRepository.save(any(User.class))).thenReturn(user);
//
//            User response = userService.updateLastName(user.getEmail(), "newLastName");
//
//            assertEquals(user, response);
//            assertEquals("newLastName", response.getLastName());
//            verify(userRepository, times(1)).findByUserId(anyString());
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test update user's last name should throw UserNotFoundException when user is not found")
//        void testUpdateLastNameShouldThrowWhenUserNotFound() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.updateLastName(user.getEmail(), "newLastName");
//            });
//
//            verify(userRepository, times(1)).findByUserId(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Test update user's phone number")
//    class UpdatePhoneNumber {
//        @Test
//        @DisplayName("Should update user's phone number successfully")
//        void testUpdatePhoneNumber() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
//            when(userRepository.save(any(User.class))).thenReturn(user);
//
//            User response = userService.updatePhoneNumber(user.getEmail(), "newPhoneNumber");
//
//            assertEquals(user, response);
//            assertEquals("newPhoneNumber", response.getPhone());
//            verify(userRepository, times(1)).findByUserId(anyString());
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test update user's phone number should throw UserNotFoundException when user is not found")
//        void testUpdatePhoneNumberShouldThrowWhenUserNotFound() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.updatePhoneNumber(user.getEmail(), "newPhoneNumber");
//            });
//
//            verify(userRepository, times(1)).findByUserId(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Test update user's address")
//    class UpdateAddress {
//        @Test
//        @DisplayName("Should update user's address successfully")
//        void testUpdateAddress() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
//            when(userRepository.save(any(User.class))).thenReturn(user);
//
//            User response = userService.updateAddress(user.getEmail(), "newAddress");
//
//            assertEquals(user, response);
//            assertEquals("newAddress", response.getAddress());
//            verify(userRepository, times(1)).findByUserId(anyString());
//            verify(userRepository, times(1)).save(any(User.class));
//        }
//
//        @Test
//        @DisplayName("Test update user's address should throw UserNotFoundException when user is not found")
//        void testUpdateAddressShouldThrowWhenUserNotFound() throws UserNotFoundException {
//            User user = new User();
//            user.setUserId("1");
//            user.setEmail("leeroy");
//            user.setPassword("password");
//            user.setRole(Role.ROLE_USER);
//            user.setAuthorities(Role.ROLE_USER.getAuthorities());
//
//            when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> {
//                userService.updateAddress(user.getEmail(), "newAddress");
//            });
//
//            verify(userRepository, times(1)).findByUserId(anyString());
//            verify(userRepository, never()).save(any(User.class));
//        }
//    }
}
