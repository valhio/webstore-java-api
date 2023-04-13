package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.exception.domain.OrderNotFoundException;
import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.Order;
import com.github.valhio.storeapi.model.OrderItem;
import com.github.valhio.storeapi.model.Product;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.repository.OrderRepository;
import com.github.valhio.storeapi.service.ProductService;
import com.github.valhio.storeapi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Test find all orders")
    void testFindAllOrders() {
        when(repository.findAllOrderByOrderDateDesc()).thenReturn(List.of(new Order(), new Order()));

        Iterable<Order> all = orderService.findAll();

        assertEquals(2, all.spliterator().getExactSizeIfKnown());
        verify(repository, times(1)).findAllOrderByOrderDateDesc();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Test find all orders by user id")
    void testFindAllOrdersByUserId() {
        when(repository.findAllByUserIdOrderByOrderDateDesc("userId")).thenReturn(List.of(new Order(), new Order()));

        Iterable<Order> all = orderService.findAllByUserId("userId");

        assertEquals(2, all.spliterator().getExactSizeIfKnown());
        verify(repository, times(1)).findAllByUserIdOrderByOrderDateDesc("userId");
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Test find all orders by user email")
    void testFindAllOrdersByUserEmail() {
        when(repository.findAllByEmail("userId")).thenReturn(List.of(new Order(), new Order()));

        Iterable<Order> all = orderService.findAllByEmail("userId");

        assertEquals(2, all.spliterator().getExactSizeIfKnown());
        verify(repository, times(1)).findAllByEmail("userId");
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Test find all orders by user")
    void testFindAllOrdersByUser() {
        User user = new User();
        when(repository.findAllByUserOrderByOrderDateDesc(user)).thenReturn(List.of(new Order(), new Order()));

        Iterable<Order> all = orderService.findAllByUser(user);

        assertEquals(2, all.spliterator().getExactSizeIfKnown());
        verify(repository, times(1)).findAllByUserOrderByOrderDateDesc(user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Test update order")
    void testUpdateOrder() {
        Order order = new Order();
        when(repository.save(order)).thenReturn(order);

        Order result = orderService.updateOrder(order);

        assertEquals(order, result);
        verify(repository).save(order);
        verifyNoMoreInteractions(repository);
    }

    @Nested
    @DisplayName("Test createOrder")
    class CreateOrder {

        @Test
        @DisplayName("Test createOrder with null userId")
        void testCreateOrderWithNullUserId() throws UserNotFoundException {
            Order order = new Order();
            order.setOrderItems(Collections.emptyList());
            when(repository.save(order)).thenReturn(order);
            Order result = orderService.createOrder(order, null);
            assertEquals(order, result);
            verify(repository).save(order);
            verifyNoInteractions(userService, productService);
        }

        @Test
        @DisplayName("Test createOrder with non-null userId")
        void testCreateOrderWithNonNullUserId() throws UserNotFoundException {
            User user = new User();
            Order order = new Order();
            order.setOrderItems(Collections.emptyList());
            when(userService.findUserByUserId("userId")).thenReturn(user);
            when(repository.save(order)).thenReturn(order);
            Order result = orderService.createOrder(order, "userId");
            assertEquals(order, result);
            verify(repository).save(order);
            verify(userService).findUserByUserId("userId");
            verifyNoMoreInteractions(userService, productService);
        }

        @Test
        @DisplayName("Test createOrder with ProductNotFoundException")
        void testCreateOrderWithProductNotFoundException() throws UserNotFoundException, ProductNotFoundException {
            Product product = new Product();
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(10);
            Order order = new Order();
            order.setOrderItems(Collections.singletonList(orderItem));
            when(userService.findUserByUserId("userId")).thenReturn(new User());
            doThrow(new ProductNotFoundException("Product not found")).when(productService).removeQuantity(product.getId(), orderItem.getQuantity());

            orderService.createOrder(order, "userId");

            verify(userService).findUserByUserId("userId");
            verify(productService).removeQuantity(product.getId(), orderItem.getQuantity());
            verifyNoMoreInteractions(userService, productService);
        }
    }

    @Nested
    @DisplayName("Test find order by id")
    class TestFindOrderById {

        @Test
        @DisplayName("Test find order by id with valid id")
        void testFindOrderByIdWithNonNullId() throws OrderNotFoundException {
            Order order = new Order();
            when(repository.findById(1L)).thenReturn(java.util.Optional.of(order));

            Order result = orderService.findById(1L);

            assertEquals(order, result);
            verify(repository).findById(1L);
            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("Test find order by id with invalid id throws OrderNotFoundException")
        void testFindOrderByIdWithInvalidId() {
            when(repository.findById(1L)).thenReturn(java.util.Optional.empty());

            OrderNotFoundException orderNotFoundException = assertThrows(OrderNotFoundException.class, () -> orderService.findById(1L));

            assertEquals("Order not found", orderNotFoundException.getMessage());
            verify(repository).findById(1L);
            verifyNoMoreInteractions(repository);
        }
    }


}