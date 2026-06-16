package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.models.Review;
import com.makemytrip.makemytrip.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(LocalDateTime.now());
        }
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForFlight(String flightId) {
        return reviewRepository.findByTargetTypeAndTargetId(Review.TargetType.FLIGHT, flightId);
    }

    public List<Review> getReviewsForHotel(String hotelId) {
        return reviewRepository.findByTargetTypeAndTargetId(Review.TargetType.HOTEL, hotelId);
    }
}
