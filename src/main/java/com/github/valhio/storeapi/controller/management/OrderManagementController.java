package com.github.valhio.storeapi.controller.management;

import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.domain.UserNotFoundException;
import com.github.valhio.storeapi.model.Order;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.service.OrderService;
import com.github.valhio.storeapi.service.UserService;
import com.github.valhio.storeapi.utility.JWTTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("api/v1/orders/management")
public class OrderManagementController {

    private final OrderService orderService;
    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;


    public OrderManagementController(OrderService orderService, UserService userService, JWTTokenProvider jwtTokenProvider) {
        this.orderService = orderService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('UPDATE')")
    public ResponseEntity<Iterable<Order>> findAllOrders(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!isAuthorized(authorizationHeader))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String token = authorizationHeader.substring(7);

        if (!jwtTokenProvider.hasRole(token, new Role[]{Role.ROLE_ADMIN, Role.ROLE_SUPER_ADMIN, Role.ROLE_MANAGER}))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/user/{userId}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    public ResponseEntity<Iterable<Order>> getOrdersByUserId(@PathVariable String userId, @RequestBody User user, HttpServletRequest request) throws UserNotFoundException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!isAuthorized(authorizationHeader))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User byUserId = userService.findUserByUserId(userId);

        String token = authorizationHeader.substring(7);

        if (!jwtTokenProvider.isSuperAdmin(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(orderService.findAllByUser(byUserId));
    }

    public boolean isAuthorized(String token) {
        return (token != null && token.startsWith("Bearer "));
    }
}
