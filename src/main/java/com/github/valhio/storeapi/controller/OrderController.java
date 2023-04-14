package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.enumeration.OrderItemStatus;
import com.github.valhio.storeapi.enumeration.OrderStatus;
import com.github.valhio.storeapi.enumeration.PaymentMethod;
import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.domain.OrderItemNotFoundException;
import com.github.valhio.storeapi.exception.domain.OrderNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.*;
import com.github.valhio.storeapi.repository.ProductRepository;
import com.github.valhio.storeapi.request.OrderItemRequest;
import com.github.valhio.storeapi.request.OrderRequest;
import com.github.valhio.storeapi.service.OrderService;
import com.github.valhio.storeapi.service.UserService;
import com.github.valhio.storeapi.utility.JWTTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
    The OrderController class is a REST API controller for handling orders in a store. It contains several API endpoints for creating,
    retrieving, and updating orders, as well as retrieving orders for a particular user or email address.
    Here is a breakdown of the key features of the controller:

    The newOrder method creates a new order by parsing the order request payload and creating a new Order object with the given data.
    The method also sets the current date and time as the order date, and sets the order status to "ORDER_PLACED".
    The method then calls the createOrder method of the OrderService to persist the order to a database.

    The getOrdersByUserId and getOrdersByUserEmail methods retrieve orders from the database for a particular user
    by their user ID or email address, respectively. These methods are restricted to users with the appropriate permissions,
    as specified by the PreAuthorize annotations.

    The getOrderById method retrieves a single order from the database by its ID. This method is also restricted to authorized users,
    and uses JWT authentication to ensure that the requester has the appropriate permissions to access the order.

    The updateOrderStatus and updateOrderItemStatus methods update the status of an order or order item, respectively.
    These methods are also restricted to authorized users with the appropriate permissions.

    The controller also includes some exception handling for cases where the requested order or user cannot be found.

    Overall, this controller provides a set of endpoints for creating, retrieving, and updating orders in a store,
    with appropriate security and access restrictions in place to ensure that only authorized users can access or modify the orders.
*/

@CrossOrigin
@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final JWTTokenProvider jwtTokenProvider;


    public OrderController(OrderService orderService, UserService userService, ProductRepository productRepository, JWTTokenProvider jwtTokenProvider) {
        this.orderService = orderService;
        this.userService = userService;
        this.productRepository = productRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @RequestMapping("/new")
    @ResponseBody
    public ResponseEntity<Order> newOrder(@RequestBody OrderRequest orderRequest) throws UserNotFoundException {
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest orderItemRequest : orderRequest.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            Product product = productRepository.findById(orderItemRequest.getProductId()).orElse(null);
            orderItem.setProduct(product); // Set product. If product not found, set null
            orderItem.setAvailableQuantity(product != null ? product.getQuantity() : 0);
            orderItem.setProductName(orderItemRequest.getProductName());
            orderItem.setQuantity(orderItemRequest.getQuantity());
            orderItem.setPricePerItem(orderItemRequest.getPricePerItem());
            orderItem.setStatus(OrderItemStatus.ORDER_PLACED);
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        order.setFirstName(orderRequest.getFirstName());
        order.setLastName(orderRequest.getLastName());
        order.setEmail(orderRequest.getEmail());
        order.setPhone(orderRequest.getPhone());
        order.setAddress(orderRequest.getAddress());
        order.setNotes(orderRequest.getNotes());
        order.setCity(orderRequest.getCity());
        order.setZipCode(orderRequest.getZipCode());
        order.setCountry(orderRequest.getCountry());
        order.setPaymentMethod(PaymentMethod.valueOf(orderRequest.getPaymentMethod()));
        order.setProductsTotal(orderRequest.getProductsTotal());
        order.setDeliveryFee(orderRequest.getDeliveryFee());
        order.setDiscount(orderRequest.getDiscount());
        order.setTotalAmount(orderRequest.getTotalAmount());
        order.setOrderStatus(OrderStatus.ORDER_PLACED);
        order.setOrderDate(LocalDateTime.now());
        Order newOrder = orderService.createOrder(order, orderRequest.getUserId());
        return ResponseEntity.ok(newOrder);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("#auth.userId == #userId or hasAnyRole('ROLE_MANAGER','ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Iterable<Order>> getOrdersByUserId(@AuthenticationPrincipal UserPrincipal auth, @PathVariable String userId, HttpServletRequest request) throws UserNotFoundException {
        User byUserId = userService.findUserByUserId(userId);
        return ResponseEntity.ok(orderService.findAllByUserId(byUserId.getUserId()));
    }

    @GetMapping("/user/email/{email}")
    @PreAuthorize("#auth.email == #email or hasAnyRole('ROLE_MANAGER','ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Iterable<Order>> getOrdersByUserEmail(@AuthenticationPrincipal UserPrincipal auth, @PathVariable String email, HttpServletRequest request) {
        return ResponseEntity.ok(orderService.findAllByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@AuthenticationPrincipal UserPrincipal auth, @PathVariable Long id, HttpServletRequest request) throws OrderNotFoundException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        Order order = orderService.findById(id);

        if (jwtTokenProvider.hasRole(token, new Role[]{Role.ROLE_ADMIN, Role.ROLE_SUPER_ADMIN, Role.ROLE_MANAGER}))
            return ResponseEntity.ok(order);

        if (!order.getUser().getEmail().equals(jwtTokenProvider.getSubject(token)))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status/{status}")
    @PreAuthorize("hasAnyAuthority('UPDATE') or hasAnyRole('ROLE_MANAGER','ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable String orderId, @PathVariable String status, HttpServletRequest request) throws OrderNotFoundException {
        Order order = orderService.findById(Long.parseLong(orderId));
        order.setOrderStatus(OrderStatus.valueOf(status));
        return ResponseEntity.ok(orderService.updateOrder(order));
    }

    @PutMapping("/{orderId}/orderItem/{itemId}/status/{status}")
    @PreAuthorize("hasAnyAuthority('UPDATE') or hasAnyRole('ROLE_MANAGER','ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Order> updateOrderItemStatus(@PathVariable String orderId, @PathVariable String itemId, @PathVariable String status, HttpServletRequest request) throws OrderNotFoundException, OrderItemNotFoundException {
        Order order = orderService.findById(Long.parseLong(orderId));
        OrderItem orderItem = order.getOrderItems()
                .stream()
                .filter(item -> item.getId().equals(Long.parseLong(itemId)))
                .findFirst()
                .orElseThrow(() -> new OrderItemNotFoundException("Order item not found"));

        orderItem.setStatus(OrderItemStatus.valueOf(status));
        return ResponseEntity.ok(orderService.updateOrder(order));
    }

}
