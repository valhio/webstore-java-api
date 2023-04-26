package com.github.valhio.storeapi.controller;

import com.github.valhio.storeapi.service.ReviewCommentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/review-comment")
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;

    public ReviewCommentController(ReviewCommentService reviewCommentService) {
        this.reviewCommentService = reviewCommentService;
    }


}
