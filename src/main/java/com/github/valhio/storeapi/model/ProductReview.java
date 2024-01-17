package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product_reviews")
public class ProductReview {

    @Id
    private String id;

    private int rating;

    private String title;

    @Field("review_text")
    private String reviewText;

    @DBRef(lazy = true)
    @JsonIgnoreProperties("productReviews")
    private Product product;

    @DBRef
    private User user;

    @DBRef
    private List<ReviewLike> likes = new ArrayList<>();

    @DBRef
    private List<ReviewComment> comments = new ArrayList<>();

    @Field("review_date")
    private Date reviewDate;
}
