package com.github.valhio.storeapi.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductReviewRequest {

    private int rating;
    private String title;
    private String reviewText;
    private String productId;
    private String userId;

}
