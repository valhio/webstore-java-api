package com.github.valhio.storeapi.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private String userId;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String address;

    private String notes;

    private String city;

    private String zipCode;

    private String country;

    private Double productsTotal;

    private Double deliveryFee;

    private Double totalAmount;

    private Double discount;

    private String paymentMethod;

    private List<OrderItemRequest> orderItems;
}
