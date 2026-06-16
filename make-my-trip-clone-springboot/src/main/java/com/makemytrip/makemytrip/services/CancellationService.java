package com.makemytrip.makemytrip.services;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.models.Refund;
import com.makemytrip.makemytrip.models.RefundStatus;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.repositories.RefundRepository;
import com.makemytrip.makemytrip.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CancellationService {
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefundRepository refundRepository;

    public Refund cancelBooking(String bookingId) {
        Optional<Refund> existingRefund = refundRepository.findByBookingId(bookingId);
        if (existingRefund.isPresent()) {
            return existingRefund.get();
        }

        Optional<Users> userOptional = userRepository.findAll().stream()
                .filter(user -> user.getBookings().stream().anyMatch(booking -> bookingId.equals(booking.getBookingId())))
                .findFirst();

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }

        Users user = userOptional.get();
        Users.Booking booking = user.getBookings().stream()
                .filter(item -> bookingId.equals(item.getBookingId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if ("Flight".equalsIgnoreCase(booking.getType())) {

    flightRepository.findById(booking.getBookingId())
            .ifPresent(flight -> {
                flight.setAvailableSeats(
                        flight.getAvailableSeats() + booking.getQuantity()
                );
                flightRepository.save(flight);
            });

} else if ("Hotel".equalsIgnoreCase(booking.getType())) {

    hotelRepository.findById(booking.getBookingId())
            .ifPresent(hotel -> {
                hotel.setAvailableRooms(
                        hotel.getAvailableRooms() + booking.getQuantity()
                );
                hotelRepository.save(hotel);
            });
}

user.getBookings().removeIf(
        item -> bookingId.equals(item.getBookingId())
);

userRepository.save(user);

        double refundAmount = calculateRefund(booking);

        Refund refund = new Refund();
        refund.setBookingId(bookingId);
        refund.setUserEmail(user.getEmail());
        refund.setRefundAmount(refundAmount);
        refund.setRefundStatus(RefundStatus.PENDING);
        refund.setCancelledAt(LocalDateTime.now());

        return refundRepository.save(refund);
    }

    public Refund getRefund(String bookingId) {
        return refundRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Refund record not found"));
    }

    private double calculateRefund(Users.Booking booking) {

        int quantity = booking.getQuantity();
        if (quantity <= 2) {
            return booking.getTotalPrice() * 0.90;
        }
        if (quantity <= 5) {
            return booking.getTotalPrice() * 0.70;
        }
        return booking.getTotalPrice() * 0.50;
    }
}