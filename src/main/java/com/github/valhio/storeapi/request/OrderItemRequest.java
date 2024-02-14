package com.github.valhio.storeapi.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    private String productId;
    private String productName;
    private Integer quantity;
    private Double pricePerItem;
}
