package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.valhio.storeapi.enumeration.OrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order_items")
public class OrderItem extends Auditable<String> {

    @Id
    private String id;

//    @JsonIgnore // to avoid infinite recursion
    @JsonIgnoreProperties("orderItems")
    private Order order;

    @JsonIgnoreProperties("productReviews")
    private Product product;

    private String productName;

    private int quantity;

    private double pricePerItem;

    private OrderItemStatus status;

    private int availableQuantity;

}

