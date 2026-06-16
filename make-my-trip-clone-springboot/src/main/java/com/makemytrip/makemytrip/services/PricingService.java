package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    public double calculateFlightPrice(Flight flight) {
        int availableSeats = flight.getAvailableSeats();

        if (availableSeats > 50) {
            return flight.getPrice();
        }
        if (availableSeats >= 20) {
            return flight.getPrice() * 1.10;
        }
        return flight.getPrice() * 1.25;
    }

    public double calculateHotelPrice(Hotel hotel) {
        int availableRooms = hotel.getAvailableRooms();

        if (availableRooms > 20) {
            return hotel.getPricePerNight();
        }
        if (availableRooms >= 5) {
            return hotel.getPricePerNight() * 1.10;
        }
        return hotel.getPricePerNight() * 1.25;
    }
}
