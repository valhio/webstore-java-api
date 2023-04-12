package com.github.valhio.storeapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valhio.storeapi.config.SecurityConfiguration;
import com.github.valhio.storeapi.enumeration.OrderItemStatus;
import com.github.valhio.storeapi.enumeration.OrderStatus;
import com.github.valhio.storeapi.enumeration.Role;
import com.github.valhio.storeapi.exception.domain.OrderNotFoundException;
import com.github.valhio.storeapi.filter.JWTAccessDeniedHandler;
import com.github.valhio.storeapi.filter.JWTAuthenticationEntryPoint;
import com.github.valhio.storeapi.filter.JWTAuthorizationFilter;
import com.github.valhio.storeapi.model.Order;
import com.github.valhio.storeapi.model.OrderItem;
import com.github.valhio.storeapi.model.User;
import com.github.valhio.storeapi.model.UserPrincipal;
import com.github.valhio.storeapi.repository.ProductRepository;
import com.github.valhio.storeapi.request.OrderItemRequest;
import com.github.valhio.storeapi.request.OrderRequest;
import com.github.valhio.storeapi.service.OrderService;
import com.github.valhio.storeapi.service.UserService;
import com.github.valhio.storeapi.utility.JWTTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import({SecurityConfiguration.class, JWTAuthorizationFilter.class, JWTAccessDeniedHandler.class, JWTAuthenticationEntryPoint.class})
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
class OrderControllerTest {

    @MockBean
    private OrderService orderService;
    @MockBean
    private UserService userService;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private JWTTokenProvider jwtTokenProvider;
    // This is required for the tests to run, otherwise it will throw a stack overflow error.
    // Spring Security requires an AuthenticationManager to be present in the context.
    // Apparently, it is a bug in Spring Framework, and it will(should) be fixed in the next release(v5.3.24). Current version is 5.3.23.
    @MockBean
    private AuthenticationManager authenticationManager;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private OrderRequest orderRequest;

    private Order order;

    @BeforeEach
    void setUp() {
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId(1L);
        orderItemRequest.setQuantity(1);
        orderItemRequest.setPricePerItem(10.0);
        orderItemRequest.setProductName("Product 1");
        OrderItemRequest orderItemRequest2 = new OrderItemRequest();
        orderItemRequest2.setProductId(2L);
        orderItemRequest2.setQuantity(1);
        orderItemRequest2.setPricePerItem(10.0);
        orderItemRequest2.setProductName("Product 2");

        orderRequest = new OrderRequest();
        orderRequest.setUserId("1");
        orderRequest.setOrderItems(List.of(orderItemRequest, orderItemRequest2));
        orderRequest.setFirstName("John");
        orderRequest.setLastName("Doe");
        orderRequest.setEmail("john@doe");
        orderRequest.setPhone("123456789");
        orderRequest.setAddress("123 Main St");
        orderRequest.setNotes("Notes");
        orderRequest.setCity("City");
        orderRequest.setZipCode("12345");
        orderRequest.setCountry("Country");
        orderRequest.setPaymentMethod("CARD_PAYMENT");
        orderRequest.setProductsTotal(20.0);
        orderRequest.setDeliveryFee(5.0);
        orderRequest.setDiscount(0.0);
        orderRequest.setTotalAmount(25.0);

        order = new Order();
        order.setId(1L);
    }

    @Nested
    @DisplayName("Create new order")
    class CreateNewOrder {

        @Test
        @DisplayName("Should return 200 when order is created")
        @WithMockUser(username = "user", password = "password", roles = "USER")
        void shouldReturn200WhenOrderIsCreated() throws Exception {
            // When
            when(orderService.createOrder(any(), any())).thenReturn(new Order());

            // Then
            mockMvc.perform(post("/api/v1/orders/new")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(orderRequest)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Get orders by userId")
    class GetOrdersByUserId {

        @Test
        @DisplayName("Should return 200 if authenticated user's userId is the same as the one in the request (the path variable)")
        void shouldReturn200WhenUserIdMatchesRequestUserId() throws Exception {
            User user = new User();
            user.setUserId("1");
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);
            // When
            when(userService.findUserByUserId(any())).thenReturn(user);
            when(orderService.findAllByUserId(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/1")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)));

            verify(userService, times(1)).findUserByUserId(any());
            verify(orderService, times(1)).findAllByUserId(any());
        }

        @Test
        @DisplayName("Should return 200 if the user is authorized with role ADMIN")
        void shouldReturn200WhenUserHasRoleAdmin() throws Exception {
            User user = new User();
            user.setUserId("12345678");
            user.setRole(Role.ROLE_ADMIN);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(userService.findUserByUserId(any())).thenReturn(user);
            when(orderService.findAllByUserId(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/1")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)));


            verify(userService, times(1)).findUserByUserId(any());
            verify(orderService, times(1)).findAllByUserId(any());
        }

        @Test
        @DisplayName("Should return 200 if the user is authorized with role MANAGER")
        void shouldReturn200WhenUserHasRoleManager() throws Exception {
            User user = new User();
            user.setUserId("12345678");
            user.setRole(Role.ROLE_MANAGER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(userService.findUserByUserId(any())).thenReturn(user);
            when(orderService.findAllByUserId(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/1")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)));


            verify(userService, times(1)).findUserByUserId(any());
            verify(orderService, times(1)).findAllByUserId(any());
        }

        @Test
        @DisplayName("Should return 200 if the user is authorized with role SUPER_ADMIN")
        void shouldReturn200WhenUserHasRoleSuperAdmin() throws Exception {
            User user = new User();
            user.setUserId("12345678");
            user.setRole(Role.ROLE_SUPER_ADMIN);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(userService.findUserByUserId(any())).thenReturn(user);
            when(orderService.findAllByUserId(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/1")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)));


            verify(userService, times(1)).findUserByUserId(any());
            verify(orderService, times(1)).findAllByUserId(any());
        }


        @Test
        @DisplayName("Should throw 401 Unauthorized when authenticated user's userId does not match the userId path variable")
        void shouldThrow401WhenUserIdPathVarDoesNotMatchAuthUserID() throws Exception {
            User user = new User();
            user.setUserId("12345678");
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(userService.findUserByUserId(any())).thenReturn(user);
            when(orderService.findAllByUserId(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/1")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isUnauthorized());

            verify(userService, times(0)).findUserByUserId(any());
            verify(orderService, times(0)).findAllByUserId(any());
        }

        @Test
        @DisplayName("Should throw 401 Unauthorized when authenticated user's userId does not match the userId path variable and user has role of USER")
        void shouldThrow401WhenUserIdPathVarDoesNotMatchAuthUserIdAndRoleIsUser() throws Exception {
            User user = new User();
            user.setUserId("12345678");
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(userService.findUserByUserId(any())).thenReturn(user);
            when(orderService.findAllByUserId(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/1")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isUnauthorized());

            verify(userService, times(0)).findUserByUserId(any());
            verify(orderService, times(0)).findAllByUserId(any());
        }

    }

    @Nested
    @DisplayName("Get orders by email")
    class GetOrdersByUserEmail {

        @Test
        @DisplayName("Should return 200 if authenticated user's email matches the one in the request (the path variable)")
        void shouldReturn200WhenUserEmailMatchesRequestEmail() throws Exception {
            User user = new User();
            user.setEmail("leeroy@jenkins");
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);
            // When
            when(orderService.findAllByEmail(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/email/" + user.getEmail())
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)));

            verify(orderService, times(1)).findAllByEmail(any());
        }

        @Test
        @DisplayName("Should return 200 if user is authorized with role ADMIN")
        void shouldReturn200WhenUserHasRoleAdmin() throws Exception {
            User user = new User();
            user.setEmail("leeroy@jenkins");
            user.setRole(Role.ROLE_ADMIN);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findAllByEmail(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/email/somethingthatdoesntmatch")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)));


            verify(orderService, times(1)).findAllByEmail(any());
        }

        @Test
        @DisplayName("Should return 200 if user is authorized with role MANAGER")
        void shouldReturn200WhenUserHasRoleManager() throws Exception {
            User user = new User();
            user.setEmail("leeroy@jenkins");
            user.setRole(Role.ROLE_MANAGER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findAllByEmail(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/email/somethingthatdoesntmatch")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)));


            verify(orderService, times(1)).findAllByEmail(any());
        }

        @Test
        @DisplayName("Should return 200 if user is authorized with role SUPER_ADMIN")
        void shouldReturn200WhenUserHasRoleSuperAdmin() throws Exception {
            User user = new User();
            user.setEmail("leeroy@jenkins");
            user.setRole(Role.ROLE_SUPER_ADMIN);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findAllByEmail(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/email/somethingthatdoesntmatch")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)));


            verify(orderService, times(1)).findAllByEmail(any());
        }

        @Test
        @DisplayName("Should throw 401 Unauthorized when authenticated user's email does not match the email path variable")
        void shouldThrow401WhenEmailPathVarDoesNotMatchAuthUserEmail() throws Exception {
            User user = new User();
            user.setEmail("leeroy@jenkins");
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findAllByEmail(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/email/somethingthatdoesntmatch")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isUnauthorized());

            verify(orderService, times(0)).findAllByEmail(any());
        }

        @Test
        @DisplayName("Should throw 401 Unauthorized when authenticated user's email does not match the email path variable and user has role of USER")
        void shouldThrow401WhenEmailPathVarDoesNotMatchAuthUserIdAndRoleIsUser() throws Exception {
            User user = new User();
            user.setEmail("leeroy@jenkins");
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findAllByEmail(any())).thenReturn(List.of(order, new Order()));

            // Then
            mockMvc.perform(get("/api/v1/orders/user/email/somethingthatdoesntmatch")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isUnauthorized());

            verify(orderService, times(0)).findAllByEmail(any());
        }

    }

    @Nested
    @DisplayName("Update order status")
    class UpdateOrderStatus {

        @Test
        @DisplayName("Should return 200 if the user has UPDATE authority")
        void shouldReturn200IfUserHasUpdateAuthority() throws Exception {
            order.setOrderStatus(OrderStatus.PENDING);
            User user = new User();
            user.setAuthorities(new String[]{"UPDATE"});
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenReturn(order);
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/1/status/PENDING")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.orderStatus", is("PENDING")));

            verify(orderService, times(1)).findById(any());
            verify(orderService, times(1)).updateOrder(any());
        }

        @Test
        @DisplayName("Should return 200 if the user has ADMIN role")
        void shouldReturn200IfUserHasAdminRole() throws Exception {
            order.setOrderStatus(OrderStatus.PENDING);
            User user = new User();
            user.setAuthorities(new String[]{"READ"});
            user.setRole(Role.ROLE_ADMIN);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenReturn(order);
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/1/status/PENDING")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.orderStatus", is("PENDING")));

            verify(orderService, times(1)).findById(any());
            verify(orderService, times(1)).updateOrder(any());
        }

        @Test
        @DisplayName("Should return 200 if the user has MANAGER role")
        void shouldReturn200IfUserHasManagerRole() throws Exception {
            order.setOrderStatus(OrderStatus.PENDING);
            User user = new User();
            user.setAuthorities(new String[]{"READ"});
            user.setRole(Role.ROLE_MANAGER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenReturn(order);
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/1/status/PENDING")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.orderStatus", is("PENDING")));

            verify(orderService, times(1)).findById(any());
            verify(orderService, times(1)).updateOrder(any());
        }

        @Test
        @DisplayName("Should return 200 if the user has SUPER_ADMIN role")
        void shouldReturn200IfUserHasSuperAdminRole() throws Exception {
            order.setOrderStatus(OrderStatus.PENDING);
            User user = new User();
            user.setAuthorities(new String[]{"READ"});
            user.setRole(Role.ROLE_SUPER_ADMIN);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenReturn(order);
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/1/status/PENDING")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.orderStatus", is("PENDING")));

            verify(orderService, times(1)).findById(any());
            verify(orderService, times(1)).updateOrder(any());
        }

        @Test
        @DisplayName("Should return 401 if the user does not have UPDATE authority or allowed role")
        void shouldReturn401IfUserDoesNotHaveUpdateAuthorityOrAllowedRole() throws Exception {
            order.setOrderStatus(OrderStatus.PENDING);
            User user = new User();
            user.setAuthorities(new String[]{"READ"});
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenReturn(order);
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/1/status/PENDING")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isUnauthorized());

            verify(orderService, times(0)).findById(any());
            verify(orderService, times(0)).updateOrder(any());
        }

        @Test
        @DisplayName("Should return 404 if the order does not exist")
        void shouldReturn404IfOrderDoesNotExist() throws Exception {
            order.setOrderStatus(OrderStatus.PENDING);
            User user = new User();
            user.setAuthorities(new String[]{"UPDATE"});
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenThrow(new OrderNotFoundException("Order not found"));
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/1/status/PENDING")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).findById(any());
            verify(orderService, times(0)).updateOrder(any());
        }
    }

    @Nested
    @DisplayName("Update order's order item status")
    class UpdateOrderItemStatus {

        @Test
        @DisplayName("Should return 200 if the user has UPDATE authority")
        void shouldReturn200IfUserHasUpdateAuthority() throws Exception {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(1L);
            orderItem.setStatus(OrderItemStatus.ORDER_PLACED);
            order.setOrderStatus(OrderStatus.PENDING);
            order.setOrderItems(List.of(orderItem));

            User user = new User();
            user.setAuthorities(new String[]{"UPDATE"});
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenReturn(order);
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/{orderId}/orderItem/{itemId}/status/{status}", 1, 1, "PENDING")
                            .with(user(authenticatedUser)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.orderItems[0].status", is("PENDING")));

            verify(orderService, times(1)).findById(any());
            verify(orderService, times(1)).updateOrder(any());
        }

        @Test
        @DisplayName("Should return 200 if the user has ADMIN role")
        void shouldReturn200IfUserHasAdminRole() throws Exception {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(1L);
            orderItem.setStatus(OrderItemStatus.ORDER_PLACED);
            order.setOrderStatus(OrderStatus.PENDING);
            order.setOrderItems(List.of(orderItem));

            User user = new User();
            user.setAuthorities(new String[]{"READ"});
            user.setRole(Role.ROLE_ADMIN);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenReturn(order);
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/{orderId}/orderItem/{itemId}/status/{status}", 1, 1, "PENDING")
                            .with(user(authenticatedUser)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.orderItems[0].status", is("PENDING")));

            verify(orderService, times(1)).findById(any());
            verify(orderService, times(1)).updateOrder(any());
        }

        @Test
        @DisplayName("Should return 200 if the user has MANAGER role")
        void shouldReturn200IfUserHasManagerRole() throws Exception {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(1L);
            orderItem.setStatus(OrderItemStatus.ORDER_PLACED);
            order.setOrderStatus(OrderStatus.PENDING);
            order.setOrderItems(List.of(orderItem));

            User user = new User();
            user.setAuthorities(new String[]{"READ"});
            user.setRole(Role.ROLE_MANAGER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenReturn(order);
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/{orderId}/orderItem/{itemId}/status/{status}", 1, 1, "PENDING")
                            .with(user(authenticatedUser)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.orderItems[0].status", is("PENDING")));

            verify(orderService, times(1)).findById(any());
            verify(orderService, times(1)).updateOrder(any());
        }

        @Test
        @DisplayName("Should return 200 if the user has SUPER_ADMIN role")
        void shouldReturn200IfUserHasSuperAdminRole() throws Exception {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(1L);
            orderItem.setStatus(OrderItemStatus.ORDER_PLACED);
            order.setOrderStatus(OrderStatus.PENDING);
            order.setOrderItems(List.of(orderItem));

            User user = new User();
            user.setAuthorities(new String[]{"READ"});
            user.setRole(Role.ROLE_SUPER_ADMIN);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenReturn(order);
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/{orderId}/orderItem/{itemId}/status/{status}", 1, 1, "PENDING")
                            .with(user(authenticatedUser)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.orderItems[0].status", is("PENDING")));

            verify(orderService, times(1)).findById(any());
            verify(orderService, times(1)).updateOrder(any());
        }

        @Test
        @DisplayName("Should return 401 if the user does not have UPDATE authority or allowed role")
        void shouldReturn401IfUserDoesNotHaveUpdateAuthorityOrAllowedRole() throws Exception {
            User user = new User();
            user.setAuthorities(new String[]{"READ"});
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenReturn(order);
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/{orderId}/orderItem/{itemId}/status/{status}", 1, 1, "PENDING")
                            .with(user(authenticatedUser)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(orderService, times(0)).findById(any());
            verify(orderService, times(0)).updateOrder(any());
        }

        @Test
        @DisplayName("Should return 404 if the order does not exist")
        void shouldReturn404IfOrderDoesNotExist() throws Exception {
            order.setOrderStatus(OrderStatus.PENDING);
            User user = new User();
            user.setAuthorities(new String[]{"UPDATE"});
            user.setRole(Role.ROLE_USER);
            UserPrincipal authenticatedUser = new UserPrincipal(user);

            // When
            when(orderService.findById(any())).thenThrow(new OrderNotFoundException("Order not found"));
            when(orderService.updateOrder(any())).thenReturn(order);

            // Then
            mockMvc.perform(put("/api/v1/orders/{orderId}/orderItem/{itemId}/status/{status}", 1, 1, "PENDING")
                            .with(user(authenticatedUser)))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).findById(any());
            verify(orderService, times(0)).updateOrder(any());
        }
    }
}