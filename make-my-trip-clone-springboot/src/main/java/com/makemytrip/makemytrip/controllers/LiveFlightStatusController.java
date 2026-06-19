package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.dto.FlightStatusDTOs.*;
import com.makemytrip.makemytrip.services.LiveFlightStatusService;
import com.makemytrip.makemytrip.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * REST API for the Live Flight Status feature.
 *
 * Base path: /api/flights/live
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  Flight status                                                          │
 * │  GET  /api/flights/live/{flightId}/status          → latest status      │
 * │  GET  /api/flights/live/{flightId}/status/history  → full history       │
 * │                                                                         │
 * │  Watch-list                                                             │
 * │  POST   /api/flights/live/track/{flightId}?userId= → start tracking     │
 * │  DELETE /api/flights/live/track/{flightId}?userId= → stop tracking      │
 * │  GET    /api/flights/live/dashboard?userId=         → dashboard card    │
 * │                                                                         │
 * │  Notifications                                                          │
 * │  GET  /api/flights/live/notifications?userId=       → all notifications │
 * │  GET  /api/flights/live/notifications/unread?userId= → unread only      │
 * │  PUT  /api/flights/live/notifications/{id}/read     → mark one read     │
 * │  PUT  /api/flights/live/notifications/read-all?userId= → mark all read  │
 * │                                                                         │
 * │  Simulation (admin/testing)                                             │
 * │  POST /api/flights/live/{flightId}/simulate         → force a status    │
 * └─────────────────────────────────────────────────────────────────────────┘
 */
@RestController
@RequestMapping("/api/flights/live")
public class LiveFlightStatusController {

    private final LiveFlightStatusService liveFlightStatusService;
    private final NotificationService     notificationService;

    public LiveFlightStatusController(LiveFlightStatusService liveFlightStatusService,
                                       NotificationService notificationService) {
        this.liveFlightStatusService = liveFlightStatusService;
        this.notificationService     = notificationService;
    }

    // ── Flight status ─────────────────────────────────────────────────────

    /**
     * Returns the most recent live status for a flight.
     * The response includes the delay reason, revised schedule, and ETA.
     */
    @GetMapping("/{flightId}/status")
    public ResponseEntity<?> getFlightStatus(@PathVariable String flightId) {
        try {
            return ResponseEntity.ok(liveFlightStatusService.getFlightStatus(flightId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Full audit trail of every status snapshot recorded for this flight.
     */
    @GetMapping("/{flightId}/status/history")
    public ResponseEntity<?> getFlightStatusHistory(@PathVariable String flightId) {
        try {
            List<FlightStatusResponse> history =
                    liveFlightStatusService.getFlightStatusHistory(flightId);
            return ResponseEntity.ok(history);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── Watch-list ────────────────────────────────────────────────────────

    /**
     * Adds a flight to the user's watch-list.
     * A user can track multiple flights simultaneously.
     */
    @PostMapping("/track/{flightId}")
    public ResponseEntity<?> trackFlight(@PathVariable String flightId,
                                          @RequestParam String userId) {
        try {
            List<String> tracked = liveFlightStatusService.trackFlight(userId, flightId);
            return ResponseEntity.ok(Map.of(
                    "message",        "Now tracking flight " + flightId,
                    "trackedFlights", tracked
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Removes a flight from the user's watch-list.
     */
    @DeleteMapping("/track/{flightId}")
    public ResponseEntity<?> untrackFlight(@PathVariable String flightId,
                                            @RequestParam String userId) {
        List<String> remaining = liveFlightStatusService.untrackFlight(userId, flightId);
        return ResponseEntity.ok(Map.of(
                "message",        "Stopped tracking flight " + flightId,
                "trackedFlights", remaining
        ));
    }

    /**
     * Dashboard: compact cards for every tracked flight + unread notification count.
     * Poll this endpoint every 30 s from the frontend to keep the UI live.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<FlightDashboardResponse> getDashboard(@RequestParam String userId) {
        return ResponseEntity.ok(liveFlightStatusService.getDashboard(userId));
    }

    // ── Notifications ─────────────────────────────────────────────────────

    /**
     * All notifications for a user, ordered newest-first.
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @RequestParam String userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    /**
     * Unread notifications only — used to populate the bell-icon dropdown.
     */
    @GetMapping("/notifications/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @RequestParam String userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsForUser(userId));
    }

    /**
     * Marks a single notification as read.
     */
    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<?> markNotificationRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }

    /**
     * Bulk mark-all-read — called when the user opens the notification centre.
     */
    @PutMapping("/notifications/read-all")
    public ResponseEntity<?> markAllRead(@RequestParam String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    // ── Simulation (admin / QA) ───────────────────────────────────────────

    /**
     * Forces a specific status transition so QA can test every notification
     * scenario without waiting for the scheduler.
     *
     * Example body (JSON):
     * {
     *   "status": "DELAYED",
     *   "delayReason": "Late arriving aircraft",
     *   "delayMinutes": 60,
     *   "gate": "B7",
     *   "terminal": "T2"
     * }
     */
    @PostMapping("/{flightId}/simulate")
    public ResponseEntity<?> simulateStatusUpdate(
            @PathVariable String flightId,
            @RequestBody SimulateStatusRequest request) {
        try {
            FlightStatusResponse response =
                    liveFlightStatusService.simulateStatusUpdate(flightId, request);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
