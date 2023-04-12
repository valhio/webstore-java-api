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

}