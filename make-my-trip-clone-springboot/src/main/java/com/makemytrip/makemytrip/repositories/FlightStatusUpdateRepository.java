package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.FlightStatusUpdate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FlightStatusUpdateRepository extends MongoRepository<FlightStatusUpdate, String> {

    /** Latest update for a flight (sorted externally) */
    List<FlightStatusUpdate> findByFlightIdOrderByUpdatedAtDesc(String flightId);

    /** Most recent single update */
    Optional<FlightStatusUpdate> findTopByFlightIdOrderByUpdatedAtDesc(String flightId);
}
