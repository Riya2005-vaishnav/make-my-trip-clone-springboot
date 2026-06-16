package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public List<Flight> recommendFlights(String userId) {
        Optional<Users> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return new ArrayList<>();
        }

        Users user = userOptional.get();
        List<Flight> bookedFlights = user.getBookings().stream()
                .filter(booking -> "Flight".equalsIgnoreCase(booking.getType()))
                .map(booking -> flightRepository.findById(booking.getBookingId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        List<Flight> recommendations = new ArrayList<>();
        for (Flight bookedFlight : bookedFlights) {
            List<Flight> sameRouteFlights = flightRepository.findAll().stream()
                    .filter(flight -> !flight.getId().equals(bookedFlight.getId()))
                    .filter(flight -> bookedFlight.getFrom().equalsIgnoreCase(flight.getFrom())
                            || bookedFlight.getTo().equalsIgnoreCase(flight.getTo()))
                    .collect(Collectors.toList());
            recommendations.addAll(sameRouteFlights);
        }

        return recommendations.stream().distinct().collect(Collectors.toList());
    }

    public List<Hotel> recommendHotels(String userId) {
        Optional<Users> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return new ArrayList<>();
        }

        Users user = userOptional.get();
        List<Hotel> bookedHotels = user.getBookings().stream()
                .filter(booking -> "Hotel".equalsIgnoreCase(booking.getType()))
                .map(booking -> hotelRepository.findById(booking.getBookingId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        List<Hotel> recommendations = new ArrayList<>();
        for (Hotel bookedHotel : bookedHotels) {
            List<Hotel> sameLocationHotels = hotelRepository.findAll().stream()
                    .filter(hotel -> !hotel.getId().equals(bookedHotel.getId()))
                    .filter(hotel -> bookedHotel.getLocation().equalsIgnoreCase(hotel.getLocation()))
                    .collect(Collectors.toList());
            recommendations.addAll(sameLocationHotels);
        }

        return recommendations.stream().distinct().collect(Collectors.toList());
    }
}
