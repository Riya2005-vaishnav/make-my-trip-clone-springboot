package com.makemytrip.makemytrip.controllers;
import com.makemytrip.makemytrip.dto.DynamicPriceResponse;
import com.makemytrip.makemytrip.dto.PriceFreezeResponse;
import com.makemytrip.makemytrip.models.Users;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.FlightStatus;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.PriceHistory;
import com.makemytrip.makemytrip.repositories.UserRepository;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.services.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class RootController {
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private PricingService pricingService;
    @GetMapping("/")
    public String home() {
        return "✅ It's running on port 8080!";
    }

    @GetMapping("/hotel")
    public ResponseEntity<List<Hotel>> getallhotel(){
        List<Hotel> hotels=hotelRepository.findAll();
        hotels.forEach(hotel -> hotel.setPricePerNight(pricingService.calculateHotelPrice(hotel)));
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/flight")
    public ResponseEntity<List<Flight>> getallflights(){
        List<Flight> flights=flightRepository.findAll();
        flights.forEach(flight -> flight.setPrice(pricingService.calculateFlightPrice(flight)));
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/flight/{id}/dynamic-price")
    public ResponseEntity<DynamicPriceResponse> getFlightDynamicPrice(@PathVariable String id) {
        try {
            return ResponseEntity.ok(pricingService.getFlightPrice(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/hotel/{id}/dynamic-price")
    public ResponseEntity<DynamicPriceResponse> getHotelDynamicPrice(@PathVariable String id) {
        try {
            return ResponseEntity.ok(pricingService.getHotelPrice(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/price-history/{itemType}/{id}")
    public ResponseEntity<List<PriceHistory>> getPriceHistory(@PathVariable String itemType, @PathVariable String id) {
        return ResponseEntity.ok(pricingService.getPriceHistory(itemType, id));
    }

    @PostMapping("/flight/{id}/price-freeze")
    public ResponseEntity<PriceFreezeResponse> freezeFlightPrice(@PathVariable String id,
                                                                 @RequestParam String userId,
                                                                 @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(pricingService.freezeFlightPrice(userId, id, quantity));
    }

    @PostMapping("/hotel/{id}/price-freeze")
    public ResponseEntity<PriceFreezeResponse> freezeHotelPrice(@PathVariable String id,
                                                                @RequestParam String userId,
                                                                @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(pricingService.freezeHotelPrice(userId, id, quantity));
    }

    @GetMapping("/flight/{id}/status")
    public ResponseEntity<FlightStatus> getFlightStatus(@PathVariable String id) {
        return flightRepository.findById(id)
                .map(flight -> ResponseEntity.ok(flight.getStatus()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/flight/{id}/status")
    public ResponseEntity<FlightStatus> updateFlightStatus(@PathVariable String id,
                                                           @RequestParam FlightStatus status) {
        return flightRepository.findById(id)
                .map(flight -> {
                    flight.setStatus(status);
                    flightRepository.save(flight);
                    return ResponseEntity.ok(flight.getStatus());
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
