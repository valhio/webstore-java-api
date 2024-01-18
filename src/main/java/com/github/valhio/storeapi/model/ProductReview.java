package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.valhio.storeapi.serializer.CustomUserSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.CascadeType;
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
    @JsonSerialize(using = CustomUserSerializer.class)
    private User user;

    @DBRef
    @JsonIgnoreProperties({"review"})
    private List<ReviewLike> likes = new ArrayList<>();

    @DBRef(lazy = true)
    private List<ReviewComment> comments = new ArrayList<>();

    @Field("review_date")
    private Date reviewDate;

}
