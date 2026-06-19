package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.FlightTracker;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FlightTrackerRepository extends MongoRepository<FlightTracker, String> {

    Optional<FlightTracker> findByUserId(String userId);
}
