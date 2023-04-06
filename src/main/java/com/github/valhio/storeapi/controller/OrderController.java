package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.enumeration.OrderItemStatus;
import com.github.valhio.storeapi.enumeration.OrderStatus;
import com.github.valhio.storeapi.enumeration.PaymentMethod;
import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.domain.OrderNotFoundException;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.Order;
import com.github.valhio.storeapi.model.OrderItem;
import com.github.valhio.storeapi.model.Product;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.repository.ProductRepository;
import com.github.valhio.storeapi.request.OrderItemRequest;
import com.github.valhio.storeapi.request.OrderRequest;
import com.github.valhio.storeapi.service.OrderService;
import com.github.valhio.storeapi.service.UserService;
import com.github.valhio.storeapi.utility.JWTTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<Order> newOrder(@RequestBody OrderRequest orderRequest) {
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
        return ResponseEntity.ok(orderService.createOrder(order, orderRequest.getUserId()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Iterable<Order>> getOrdersByUserId(@PathVariable String userId, HttpServletRequest request) throws UserNotFoundException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!isAuthorized(authorizationHeader))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User byUserId = userService.findUserByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.isSuperAdmin(token)) {
            return ResponseEntity.ok(orderService.findAllByUser(byUserId));
        } else {
            return jwtTokenProvider.getSubject(token).equals(byUserId.getEmail())
                    ? ResponseEntity.ok(orderService.findAllByUser(byUserId))
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/user/email/{email}")
    public ResponseEntity<Iterable<Order>> getOrdersByUserEmail(@PathVariable String email, HttpServletRequest request) throws UserNotFoundException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!isAuthorized(authorizationHeader))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String token = authorizationHeader.substring(7);

        return jwtTokenProvider.getSubject(token).equals(email)
                ? ResponseEntity.ok(orderService.findAllByEmail(email))
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id, HttpServletRequest request) throws OrderNotFoundException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!isAuthorized(authorizationHeader))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String token = authorizationHeader.substring(7);

        Order order = orderService.findById(id);

        if (jwtTokenProvider.hasRole(token, new Role[]{Role.ROLE_ADMIN, Role.ROLE_SUPER_ADMIN, Role.ROLE_MANAGER}))
            return ResponseEntity.ok(order);

        if (!order.getUser().getEmail().equals(jwtTokenProvider.getSubject(token)))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status/{status}")
//    @PreAuthorize("hasAnyAuthority('UPDATE')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable String orderId, @PathVariable String status, HttpServletRequest request) throws OrderNotFoundException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!isAuthorized(authorizationHeader))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Order order = orderService.findById(Long.parseLong(orderId));
        order.setOrderStatus(OrderStatus.valueOf(status));
        return ResponseEntity.ok(orderService.updateOrder(order));
    }

    private boolean isAuthorized(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }

    @PutMapping("/{orderId}/orderItem/{itemId}/status/{status}")
    public ResponseEntity<Order> updateOrderItemStatus(@PathVariable String orderId, @PathVariable String itemId, @PathVariable String status, HttpServletRequest request) throws OrderNotFoundException {
        System.out.println("updateOrderItemStatus");
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!isAuthorized(authorizationHeader))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Order order = orderService.findById(Long.parseLong(orderId));
        OrderItem orderItem = order.getOrderItems().stream().filter(item -> item.getId().equals(Long.parseLong(itemId))).findFirst().orElse(null);

        if (orderItem == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        orderItem.setStatus(OrderItemStatus.valueOf(status));
        return ResponseEntity.ok(orderService.updateOrder(order));
    }


}
