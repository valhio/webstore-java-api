package com.github.valhio.storeapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Valhio
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review_like")
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY means that the entity will be loaded only when it is accessed
    @JoinColumn(name = "review_id")
    private ProductReview review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Date likeDate;

//        private boolean liked;
//
//        private boolean disliked;
//
//        private boolean reported;
//
//        private boolean deleted;
//
//        private boolean edited;
//
}
