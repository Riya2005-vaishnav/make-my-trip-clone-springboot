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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
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

    public Refund cancelBooking(String bookingId, String reason) {
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

        String resourceId = booking.getResourceId() != null ? booking.getResourceId() : booking.getBookingId();
        if ("Flight".equalsIgnoreCase(booking.getType())) {
            flightRepository.findById(resourceId)
                    .ifPresent(flight -> {
                        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getQuantity());
                        flightRepository.save(flight);
                    });
        } else if ("Hotel".equalsIgnoreCase(booking.getType())) {
            hotelRepository.findById(resourceId)
                    .ifPresent(hotel -> {
                        hotel.setAvailableRooms(hotel.getAvailableRooms() + booking.getQuantity());
                        hotelRepository.save(hotel);
                    });
        }

        double refundAmount = calculateRefund(booking);
        String policy = computeRefundPolicy(booking);
        LocalDateTime expectedCompletion = LocalDateTime.now().plusDays(3);

        booking.setCancelled(true);
        booking.setCancellationReason(reason);
        booking.setRefundAmount(refundAmount);
        booking.setRefundStatus(RefundStatus.PENDING.name());
        booking.setRefundPolicyApplied(policy);
        booking.setExpectedRefundCompletionAt(expectedCompletion.toString());

        userRepository.save(user);

        Refund refund = new Refund();
        refund.setBookingId(bookingId);
        refund.setUserId(user.getId());
        refund.setUserEmail(user.getEmail());
        refund.setRefundAmount(refundAmount);
        refund.setRefundStatus(RefundStatus.PENDING);
        refund.setCancellationReason(reason);
        refund.setRefundPolicyApplied(policy);
        refund.setCancelledAt(LocalDateTime.now());
        refund.setExpectedCompletionAt(expectedCompletion);

        return refundRepository.save(refund);
    }

    public Refund getRefund(String bookingId) {
        return refundRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Refund record not found"));
    }

    public List<Refund> getRefundsByUser(String userId) {
        return refundRepository.findByUserId(userId);
    }

    private double calculateRefund(Users.Booking booking) {
        LocalDateTime bookingTime = parseBookingTime(booking.getDate());
        if (bookingTime != null) {
            LocalDateTime cutoff = bookingTime.plusHours(24);
            if (LocalDateTime.now().isBefore(cutoff) || LocalDateTime.now().isEqual(cutoff)) {
                return booking.getTotalPrice() * 0.50;
            }
            LocalDateTime lateCutoff = bookingTime.plusDays(3);
            if (LocalDateTime.now().isBefore(lateCutoff) || LocalDateTime.now().isEqual(lateCutoff)) {
                return booking.getTotalPrice() * 0.25;
            }
        }
        return 0.0;
    }

    private String computeRefundPolicy(Users.Booking booking) {
        LocalDateTime bookingTime = parseBookingTime(booking.getDate());
        if (bookingTime != null) {
            LocalDateTime cutoff = bookingTime.plusHours(24);
            if (LocalDateTime.now().isBefore(cutoff) || LocalDateTime.now().isEqual(cutoff)) {
                return "50% refund within 24 hours of booking";
            }
            LocalDateTime lateCutoff = bookingTime.plusDays(3);
            if (LocalDateTime.now().isBefore(lateCutoff) || LocalDateTime.now().isEqual(lateCutoff)) {
                return "25% refund within 3 days of booking";
            }
        }
        return "No refund available after 3 days";
    }

    private LocalDateTime parseBookingTime(String bookingDate) {
        if (bookingDate == null || bookingDate.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(bookingDate);
        } catch (DateTimeParseException ex) {
            try {
                return LocalDate.parse(bookingDate).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }
    }
}