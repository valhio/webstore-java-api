package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.valhio.storeapi.enumeration.OrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore // to avoid infinite recursion
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price_per_item")
    private double pricePerItem;

    private OrderItemStatus status;

    @Column(name = "available_quantity")
    private int availableQuantity;

}
