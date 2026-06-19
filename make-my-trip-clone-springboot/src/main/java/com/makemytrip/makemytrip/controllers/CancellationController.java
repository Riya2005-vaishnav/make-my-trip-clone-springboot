package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.models.Refund;
import com.makemytrip.makemytrip.services.CancellationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cancellations")
@CrossOrigin(origins = "*")
public class CancellationController {

    @Autowired
    private CancellationService cancellationService;

    @PostMapping("/{bookingId}")
    public Refund cancelBooking(
            @PathVariable String bookingId,
            @RequestParam(required = false, defaultValue = "Customer requested cancellation") String reason
    ) {
        return cancellationService.cancelBooking(bookingId, reason);
    }

    @GetMapping("/refund/{bookingId}")
    public Refund getRefund(@PathVariable String bookingId) {
        return cancellationService.getRefund(bookingId);
    }

    @GetMapping("/user")
    public java.util.List<Refund> getRefundsByUser(@RequestParam String userId) {
        return cancellationService.getRefundsByUser(userId);
    }
}
