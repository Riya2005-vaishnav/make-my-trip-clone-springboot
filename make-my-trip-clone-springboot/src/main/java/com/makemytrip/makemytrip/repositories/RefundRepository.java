package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.Refund;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefundRepository extends MongoRepository<Refund, String> {
    Optional<Refund> findByBookingId(String bookingId);
}
