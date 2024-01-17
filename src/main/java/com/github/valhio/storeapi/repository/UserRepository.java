package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

//    User findByUsername(String username); // Throws NoResultException if not found

    @Query(value = "{'email': ?0}")
    Optional<User> findByEmail(String email);

//    boolean existsByUsername(String username);


    @Query(value = "{'email': ?0}")
    Optional<User> existsByEmail(String email);


    @Query(value = "{'role': ?0}")
    Optional<Collection<User>> findAllByRole(Role role);

    @Query(value = "{'userId': ?0}")
    Optional<User> findByUserId(String userId);

//    @Query(value = "SELECT u FROM User u WHERE CONCAT(u.userId , u.firstName, u.lastName, u.email) LIKE %?1%")
    @Query(value = "{$or: [{'userId': {$regex: ?0, $options: 'i'}}, {'firstName': {$regex: ?0, $options: 'i'}}, {'lastName': {$regex: ?0, $options: 'i'}}, {'email': {$regex: ?0, $options: 'i'}}]}")
    Page<User> findAll(String keyword, Pageable pageable);
}
