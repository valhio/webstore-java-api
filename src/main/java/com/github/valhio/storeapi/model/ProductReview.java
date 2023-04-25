package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_review")
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;

    private String title;

    @Column(name = "review_text", length = 750)
    private String reviewText;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties("productReviews")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Date reviewDate;

}