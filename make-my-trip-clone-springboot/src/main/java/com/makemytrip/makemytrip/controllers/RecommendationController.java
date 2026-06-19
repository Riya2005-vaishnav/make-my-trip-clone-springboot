package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.services.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/flights/{userId}")
    public List<Flight> recommendFlights(@PathVariable String userId) {
        return recommendationService.recommendFlights(userId);
    }

    @GetMapping("/hotels/{userId}")
    public List<Hotel> recommendHotels(@PathVariable String userId) {
        return recommendationService.recommendHotels(userId);
    }
}
