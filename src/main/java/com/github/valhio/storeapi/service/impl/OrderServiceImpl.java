package com.github.valhio.storeapi.service.impl;

import com.github.valhio.storeapi.exception.domain.OrderNotFoundException;
import com.github.valhio.storeapi.exception.domain.ProductNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.Order;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.repository.OrderRepository;
import com.github.valhio.storeapi.service.InvoiceService;
import com.github.valhio.storeapi.service.OrderService;
import com.github.valhio.storeapi.service.ProductService;
import com.github.valhio.storeapi.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final UserService userService;
    private final ProductService productService;
    private final InvoiceService invoiceService;

    public OrderServiceImpl(OrderRepository repository, UserService userService, ProductService productService, InvoiceService invoiceService) {
        this.repository = repository;
        this.userService = userService;
        this.productService = productService;
        this.invoiceService = invoiceService;
    }

    @Override
    public Order createOrder(Order order, String userId) throws UserNotFoundException {
        if (userId != null) {
            order.setUser(userService.findUserByUserId(userId));
        }

        // Remove quantity from product
        order.getOrderItems().forEach(item -> {
            try {
                if (item.getProduct() != null)
                    productService.removeQuantity(item.getProduct().getId(), item.getQuantity());
            } catch (ProductNotFoundException ignored) {
            }
        });
        order.setInvoice(invoiceService.createInvoiceFromOrder(order));
        return repository.save(order);
    }

    @Override
    public Iterable<Order> findAll() {
        return repository.findAllOrderByOrderDateDesc();
    }

    @Override
    public Iterable<Order> findAllByUserId(String userId) {
        return repository.findAllByUserIdOrderByOrderDateDesc(userId);
    }

    @Override
    public Iterable<Order> findAllByEmail(String email) {
        return repository.findAllByEmail(email);
    }

    @Override
    public Iterable<Order> findAllByUser(User user) {
        return repository.findAllByUserOrderByOrderDateDesc(user);
    }

    @Override
    public Order findById(Long id) throws OrderNotFoundException {
        return repository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    @Override
    public Order updateOrder(Order order) {
        return repository.save(order);
    }

    @Override
    public Order findByOrderNumber(String orderNumber) throws OrderNotFoundException {
        return repository.findByOrderNumber(orderNumber).orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

}
