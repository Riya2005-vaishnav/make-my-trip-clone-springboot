package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.dto.FlightStatusDTOs.*;
import com.makemytrip.makemytrip.models.*;
import com.makemytrip.makemytrip.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Central service for the Live Flight Status feature.
 *
 * Responsibilities:
 *  1. Managing per-user flight watch-lists (track / untrack).
 *  2. Polling the mock API and persisting status snapshots.
 *  3. Comparing old vs. new status and dispatching notifications when
 *     something meaningful changed.
 *  4. Assembling the dashboard payload.
 */
@Service
public class LiveFlightStatusService {

    private final FlightRepository              flightRepository;
    private final FlightStatusUpdateRepository  statusUpdateRepository;
    private final FlightTrackerRepository       trackerRepository;
    private final UserRepository                userRepository;
    private final MockFlightApiService          mockFlightApi;
    private final NotificationService           notificationService;

    public LiveFlightStatusService(
            FlightRepository flightRepository,
            FlightStatusUpdateRepository statusUpdateRepository,
            FlightTrackerRepository trackerRepository,
            UserRepository userRepository,
            MockFlightApiService mockFlightApi,
            NotificationService notificationService) {

        this.flightRepository      = flightRepository;
        this.statusUpdateRepository = statusUpdateRepository;
        this.trackerRepository     = trackerRepository;
        this.userRepository        = userRepository;
        this.mockFlightApi         = mockFlightApi;
        this.notificationService   = notificationService;
    }

    // ── Watch-list management ─────────────────────────────────────────────

    /**
     * Adds a flight to a user's watch-list.
     * Returns the updated list of tracked flight IDs.
     */
    public List<String> trackFlight(String userId, String flightId) {
        validateFlightExists(flightId);

        FlightTracker tracker = trackerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    FlightTracker t = new FlightTracker();
                    t.setUserId(userId);
                    return t;
                });

        if (!tracker.getTrackedFlightIds().contains(flightId)) {
            tracker.getTrackedFlightIds().add(flightId);
            tracker.setUpdatedAt(LocalDateTime.now());
            trackerRepository.save(tracker);
        }
        return tracker.getTrackedFlightIds();
    }

    /**
     * Removes a flight from a user's watch-list.
     */
    public List<String> untrackFlight(String userId, String flightId) {
        return trackerRepository.findByUserId(userId).map(tracker -> {
            tracker.getTrackedFlightIds().remove(flightId);
            tracker.setUpdatedAt(LocalDateTime.now());
            trackerRepository.save(tracker);
            return tracker.getTrackedFlightIds();
        }).orElse(Collections.emptyList());
    }

    // ── Status retrieval ──────────────────────────────────────────────────

    /**
     * Returns the latest status for a single flight.
     * Falls back to a fresh mock poll if no record exists yet.
     */
    public FlightStatusResponse getFlightStatus(String flightId) {
        Flight flight = findFlightOrThrow(flightId);

        FlightStatusUpdate update = statusUpdateRepository
                .findTopByFlightIdOrderByUpdatedAtDesc(flightId)
                .orElseGet(() -> pollAndPersist(flight));

        return toStatusResponse(flight, update);
    }

    /**
     * Returns the full history of status updates for a flight (newest first).
     */
    public List<FlightStatusResponse> getFlightStatusHistory(String flightId) {
        Flight flight = findFlightOrThrow(flightId);
        return statusUpdateRepository
                .findByFlightIdOrderByUpdatedAtDesc(flightId)
                .stream()
                .map(u -> toStatusResponse(flight, u))
                .collect(Collectors.toList());
    }

    /**
     * Dashboard view: all tracked flights with compact status + unread count.
     */
    public FlightDashboardResponse getDashboard(String userId) {
        List<String> ids = trackerRepository.findByUserId(userId)
                .map(FlightTracker::getTrackedFlightIds)
                .orElse(Collections.emptyList());

        List<TrackedFlightSummary> summaries = ids.stream()
                .map(flightId -> flightRepository.findById(flightId).map(flight -> {
                    FlightStatusUpdate u = statusUpdateRepository
                            .findTopByFlightIdOrderByUpdatedAtDesc(flightId)
                            .orElseGet(() -> pollAndPersist(flight));
                    return toSummary(flight, u);
                }).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        FlightDashboardResponse resp = new FlightDashboardResponse();
        resp.setTrackedFlights(summaries);
        resp.setUnreadNotificationCount(notificationService.getUnreadCount(userId));
        return resp;
    }

    // ── Manual simulation (admin / testing) ───────────────────────────────

    /**
     * Lets an admin (or test script) force a specific status transition
     * so QA can validate every notification scenario without waiting for the scheduler.
     */
    public FlightStatusResponse simulateStatusUpdate(String flightId,
                                                      SimulateStatusRequest req) {
        Flight flight = findFlightOrThrow(flightId);

        FlightStatusUpdate previous = statusUpdateRepository
                .findTopByFlightIdOrderByUpdatedAtDesc(flightId)
                .orElse(null);

        FlightStatus prevStatus = previous != null ? previous.getStatus() : FlightStatus.ON_TIME;

        FlightStatusUpdate update = mockFlightApi.buildManualUpdate(
                flight,
                req.getStatus(),
                req.getDelayReason(),
                req.getDelayMinutes(),
                req.getGate(),
                req.getTerminal());

        update = statusUpdateRepository.save(update);

        // Notify all users who are tracking this flight
        notifyTrackers(flightId, update, prevStatus);

        return toStatusResponse(flight, update);
    }

    // ── Scheduler-facing method ───────────────────────────────────────────

    /**
     * Called by {@link com.makemytrip.makemytrip.config.FlightStatusScheduler}
     * every 30 s.  Polls every distinct tracked flight, persists the result,
     * and fires notifications if the status changed.
     */
    public void refreshAllTrackedFlights() {
        Set<String> allTrackedIds = trackerRepository.findAll().stream()
                .flatMap(t -> t.getTrackedFlightIds().stream())
                .collect(Collectors.toSet());

        for (String flightId : allTrackedIds) {
            try {
                flightRepository.findById(flightId).ifPresent(flight -> {
                    FlightStatus prevStatus = statusUpdateRepository
                            .findTopByFlightIdOrderByUpdatedAtDesc(flightId)
                            .map(FlightStatusUpdate::getStatus)
                            .orElse(FlightStatus.ON_TIME);

                    FlightStatusUpdate newUpdate = pollAndPersist(flight);

                    // Only push a notification when something actually changed
                    if (newUpdate.getStatus() != prevStatus) {
                        notifyTrackers(flightId, newUpdate, prevStatus);
                    }
                });
            } catch (Exception ex) {
                // Log and continue — one bad flight must not abort the whole loop
                System.err.println("[LiveFlightStatus] Error refreshing flight " + flightId + ": " + ex.getMessage());
            }
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private FlightStatusUpdate pollAndPersist(Flight flight) {
        FlightStatusUpdate u = mockFlightApi.fetchLiveStatus(flight);
        return statusUpdateRepository.save(u);
    }

    private void notifyTrackers(String flightId,
                                 FlightStatusUpdate update,
                                 FlightStatus prevStatus) {
        trackerRepository.findAll().stream()
                .filter(t -> t.getTrackedFlightIds().contains(flightId))
                .forEach(t -> notificationService.sendFlightStatusNotification(
                        t.getUserId(), update, prevStatus));
    }

    private Flight findFlightOrThrow(String flightId) {
        return flightRepository.findById(flightId)
                .orElseThrow(() -> new NoSuchElementException("Flight not found: " + flightId));
    }

    private void validateFlightExists(String flightId) {
        if (!flightRepository.existsById(flightId))
            throw new NoSuchElementException("Flight not found: " + flightId);
    }

    // ── Mapping helpers ───────────────────────────────────────────────────

    private FlightStatusResponse toStatusResponse(Flight flight, FlightStatusUpdate u) {
        FlightStatusResponse r = new FlightStatusResponse();
        r.setFlightId(flight.getId());
        r.setFlightNumber(flight.getFlightName());
        r.setFrom(flight.getFrom());
        r.setTo(flight.getTo());
        r.setStatus(u.getStatus());
        r.setStatusLabel(buildStatusLabel(u));
        r.setDelayReason(u.getDelayReason());
        r.setScheduledDeparture(u.getScheduledDeparture());
        r.setScheduledArrival(u.getScheduledArrival());
        r.setRevisedDeparture(u.getRevisedDeparture());
        r.setEstimatedArrival(u.getEstimatedArrival());
        r.setDelayMinutes(u.getDelayMinutes());
        r.setGate(u.getGate());
        r.setTerminal(u.getTerminal());
        r.setLastUpdated(u.getUpdatedAt());
        return r;
    }

    private TrackedFlightSummary toSummary(Flight flight, FlightStatusUpdate u) {
        TrackedFlightSummary s = new TrackedFlightSummary();
        s.setFlightId(flight.getId());
        s.setFlightNumber(flight.getFlightName());
        s.setFrom(flight.getFrom());
        s.setTo(flight.getTo());
        s.setStatus(u.getStatus());
        s.setStatusLabel(buildStatusLabel(u));
        s.setEstimatedArrival(u.getEstimatedArrival());
        s.setDelayMinutes(u.getDelayMinutes());
        return s;
    }

    private String buildStatusLabel(FlightStatusUpdate u) {
        return switch (u.getStatus()) {
            case DELAYED   -> "Delayed by " + u.getDelayMinutes() + " min";
            case BOARDING  -> "Boarding — Gate " + u.getGate();
            case CANCELLED -> "Cancelled";
            case LANDED    -> "Landed";
            default        -> "On Time";
        };
    }
}
