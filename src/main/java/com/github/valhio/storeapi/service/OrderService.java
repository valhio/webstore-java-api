package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.OrderNotFoundException;
import com.github.valhio.storeapi.model.Order;
import com.github.valhio.storeapi.model.OrderItem;
import com.github.valhio.storeapi.model.User;

import java.util.Map;

public interface OrderService {
    Order createOrder(Order order, String userId);

    Iterable<Order> findAll();

    Iterable<Order> findAllByUserId(String userId);

    Iterable<Order> findAllByEmail(String email);

    Iterable<Order> findAllByUser(User user);

    Order findById(Long id) throws OrderNotFoundException;

    Order updateOrder(Order order);

//    Page<Order> getOrdersByUser(User user, Pageable pageable);
}
