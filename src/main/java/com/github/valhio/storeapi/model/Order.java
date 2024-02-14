package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.valhio.storeapi.enumeration.OrderStatus;
import com.github.valhio.storeapi.enumeration.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order extends Auditable<String> {

    @Id
    private String id;

    private String orderNumber;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String address;

    private String notes;

    private String city;

    private String zipCode;

    private String country;

    private LocalDateTime orderDate;

    private OrderStatus orderStatus;

    private PaymentMethod paymentMethod;

    private Double productsTotal;

    private Double deliveryFee;

    private Double discount;

    private Double totalAmount;

    private LocalDateTime deliveredDate;

    @DBRef
    private User user;

    @JsonIgnoreProperties("order")
    @DBRef(lazy = true)
    private Invoice invoice;

    @JsonIgnoreProperties("order")
    @DBRef(lazy = true)
    private List<OrderItem> orderItems;

}

