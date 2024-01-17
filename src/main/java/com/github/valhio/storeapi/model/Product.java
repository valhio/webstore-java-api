package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
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

    @Field("image_url")
    private String imageUrl;

    @JsonIgnoreProperties("product")
    @DBRef(lazy = true)
    private List<ProductReview> productReviews = new ArrayList<>();
}