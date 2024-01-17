package com.github.valhio.storeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "review_likes")
public class ReviewLike {

    @Id
    private String id;

    @JsonIgnoreProperties({"likes", "user"})
    private ProductReview review;

    private User user;

    private Date likeDate;

//    private boolean liked;
//
//    private boolean disliked;
//
//    private boolean reported;
//
//    private boolean deleted;
//
//    private boolean edited;
}
