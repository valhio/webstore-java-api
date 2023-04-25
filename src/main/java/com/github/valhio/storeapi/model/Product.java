package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("product")
    @OrderBy("reviewDate DESC")
    private Set<ProductReview> productReviews;
}