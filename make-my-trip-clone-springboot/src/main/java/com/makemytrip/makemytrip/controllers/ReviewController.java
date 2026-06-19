package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.models.Review;
import com.makemytrip.makemytrip.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/")
    public ResponseEntity<Review> createReview(@Valid @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.createReview(review));
    }

    @GetMapping("/flight/{id}")
    public ResponseEntity<List<Review>> getFlightReviews(@PathVariable String id) {
        return ResponseEntity.ok(reviewService.getReviewsForFlight(id));
    }

    @GetMapping("/hotel/{id}")
    public ResponseEntity<List<Review>> getHotelReviews(@PathVariable String id) {
        return ResponseEntity.ok(reviewService.getReviewsForHotel(id));
    }
}
