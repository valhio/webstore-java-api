package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@Setter
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    private String name;

    private String description;

    private double price;

    private int quantity;

    private String imageUrl;

//    @JsonIgnoreProperties("product")
//    private Set<ProductReview> productReviews;
}