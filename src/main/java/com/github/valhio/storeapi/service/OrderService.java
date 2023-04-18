package com.github.valhio.storeapi.service;

import com.github.valhio.storeapi.exception.domain.OrderNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.Order;
import com.github.valhio.storeapi.model.User;

public interface OrderService {
    Order createOrder(Order order, String userId) throws UserNotFoundException;

    Iterable<Order> findAll();

    Iterable<Order> findAllByUserId(String userId);

    Iterable<Order> findAllByEmail(String email);

    Iterable<Order> findAllByUser(User user);

    Order findById(Long id) throws OrderNotFoundException;

    Order updateOrder(Order order);

    Order findByOrderNumber(String orderNumber) throws OrderNotFoundException;
}
