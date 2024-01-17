package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "review_comments")
public class ReviewComment {

    @Id
    private String id;

    private String comment;

    @DBRef
    @JsonIgnoreProperties({"comments", "likes", "user"})
    private ProductReview review;

    @DBRef
    private User user;

    private Date commentDate;
}
