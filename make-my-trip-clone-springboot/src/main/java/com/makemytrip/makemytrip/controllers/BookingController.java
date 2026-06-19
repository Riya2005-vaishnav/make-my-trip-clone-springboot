package com.makemytrip.makemytrip.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.services.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/flight")
    public Users.Booking bookFlight(@RequestParam String userId,
                                    @RequestParam String flightId,
                                    @RequestParam int seats,
                                    @RequestParam(required = false) Double price,
                                    @RequestParam(required = false) String freezeId,
                                    @RequestParam(required = false) List<String> selectedSeats){
        return bookingService.bookFlight(userId,flightId,seats,freezeId,selectedSeats);
    }
    @PostMapping("/hotel")
    public Users.Booking bookhotel (@RequestParam String userId,
                                   @RequestParam String hotelId,
                                   @RequestParam int rooms,
                                   @RequestParam(required = false) Double price,
                                   @RequestParam(required = false) String freezeId,
                                   @RequestParam(required = false) List<String> selectedRooms){
        return bookingService.bookhotel(userId,hotelId,rooms,freezeId,selectedRooms);
    }
}
