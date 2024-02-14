package com.github.valhio.storeapi.repository;

import com.github.valhio.storeapi.model.Order;
import com.github.valhio.storeapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    @Query("{'orderDate': -1}")
    Iterable<Order> findAllOrderByOrderDateDesc();

    Iterable<Order> findAllByEmail(String email);

    Iterable<Order> findAllByUserOrderByOrderDateDesc(User user);

    Iterable<Order> findAllByUserIdOrderByOrderDateDesc(String userId);

    Optional<Order>findByOrderNumber(String orderNumber);

}
