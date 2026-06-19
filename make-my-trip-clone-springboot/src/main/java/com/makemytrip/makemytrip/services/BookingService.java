package com.makemytrip.makemytrip.services;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.models.Users.Booking;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.PriceFreeze;
import com.makemytrip.makemytrip.repositories.UserRepository;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private PricingService pricingService;

    public Booking bookFlight(String userId,String flightId,int seats,String freezeId,List<String> selectedSeats){
        Optional<Users> usersOptional =userRepository.findById(userId);
        Optional<Flight> flightOptional =flightRepository.findById(flightId);
        if(usersOptional.isPresent() && flightOptional.isPresent()){
            Users user=usersOptional.get();
            Flight flight=flightOptional.get();
            if(flight.getAvailableSeats() >= seats){
                flight.setAvailableSeats(flight.getAvailableSeats()- seats);
                flightRepository.save(flight);

                Booking booking=new Booking();
                booking.setType("Flight");
                booking.setBookingId(UUID.randomUUID().toString());
                booking.setResourceId(flightId);
                booking.setDate(LocalDateTime.now().toString());
                booking.setQuantity(seats);
                booking.setTotalPrice(resolveFlightTotal(userId, flightId, seats, freezeId));
                booking.setSelectedSeats(selectedSeats == null ? new ArrayList<>() : selectedSeats);
                booking.setCancelled(false);
                booking.setRefundStatus(null);
                booking.setRefundAmount(0);
                booking.setRefundPolicyApplied(null);
                booking.setExpectedRefundCompletionAt(null);
                user.getBookings().add(booking);
                userRepository.save(user);
                return booking;
            }else {
                throw new RuntimeException("Not enough seats available");
            }
        }
        throw new RuntimeException("User or flight not found");
    }
    public Booking bookhotel(String userId,String hotelId,int rooms,String freezeId,List<String> selectedRooms){
        Optional<Users> usersOptional =userRepository.findById(userId);
        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if(usersOptional.isPresent() && hotelOptional.isPresent()){
            Users user=usersOptional.get();
            Hotel hotel=hotelOptional.get();
            if(hotel.getAvailableRooms() >= rooms){
                hotel.setAvailableRooms(hotel.getAvailableRooms()- rooms);
                hotelRepository.save(hotel);

                Booking booking=new Booking();
                booking.setType("Hotel");
                booking.setBookingId(UUID.randomUUID().toString());
                booking.setResourceId(hotelId);
                booking.setDate(LocalDateTime.now().toString());
                booking.setQuantity(rooms);
                booking.setTotalPrice(resolveHotelTotal(userId, hotelId, rooms, freezeId));
                booking.setSelectedRooms(selectedRooms == null ? new ArrayList<>() : selectedRooms);
                booking.setCancelled(false);
                booking.setRefundStatus(null);
                booking.setRefundAmount(0);
                booking.setRefundPolicyApplied(null);
                booking.setExpectedRefundCompletionAt(null);
                user.getBookings().add(booking);
                userRepository.save(user);
                return booking;
            }else {
                throw new RuntimeException("Not enough rooms available");
            }
        }
        throw new RuntimeException("User or flight not found");
    }

    private double resolveFlightTotal(String userId, String flightId, int seats, String freezeId) {
        if (freezeId != null && !freezeId.isBlank()) {
            PriceFreeze freeze = pricingService.validateFreeze(freezeId, userId, "FLIGHT", flightId, seats);
            pricingService.markFreezeUsed(freeze);
            return freeze.getLockedTotalPrice();
        }
        return pricingService.getFlightPrice(flightId).getDynamicPrice() * seats;
    }

    private double resolveHotelTotal(String userId, String hotelId, int rooms, String freezeId) {
        if (freezeId != null && !freezeId.isBlank()) {
            PriceFreeze freeze = pricingService.validateFreeze(freezeId, userId, "HOTEL", hotelId, rooms);
            pricingService.markFreezeUsed(freeze);
            return freeze.getLockedTotalPrice();
        }
        return pricingService.getHotelPrice(hotelId).getDynamicPrice() * rooms;
    }
}
