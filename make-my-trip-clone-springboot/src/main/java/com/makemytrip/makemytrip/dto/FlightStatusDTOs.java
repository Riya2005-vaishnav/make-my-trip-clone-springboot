package com.makemytrip.makemytrip.dto;

import com.makemytrip.makemytrip.models.FlightStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * All DTOs for the Live Flight Status feature, grouped in one file
 * to keep the package tidy.
 */
public class FlightStatusDTOs {

    // ── Outbound ──────────────────────────────────────────────────────────

    /** Rich status card returned to the dashboard */
    public static class FlightStatusResponse {
        private String flightId;
        private String flightNumber;
        private String from;
        private String to;
        private FlightStatus status;
        private String statusLabel;          // "Delayed by 60 min"
        private String delayReason;
        private String scheduledDeparture;
        private String scheduledArrival;
        private String revisedDeparture;
        private String estimatedArrival;
        private int delayMinutes;
        private String gate;
        private String terminal;
        private LocalDateTime lastUpdated;

        // ── Getters & Setters ─────────────────────────────────────────────

        public String getFlightId() { return flightId; }
        public void setFlightId(String flightId) { this.flightId = flightId; }

        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }

        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }

        public FlightStatus getStatus() { return status; }
        public void setStatus(FlightStatus status) { this.status = status; }

        public String getStatusLabel() { return statusLabel; }
        public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }

        public String getDelayReason() { return delayReason; }
        public void setDelayReason(String delayReason) { this.delayReason = delayReason; }

        public String getScheduledDeparture() { return scheduledDeparture; }
        public void setScheduledDeparture(String scheduledDeparture) { this.scheduledDeparture = scheduledDeparture; }

        public String getScheduledArrival() { return scheduledArrival; }
        public void setScheduledArrival(String scheduledArrival) { this.scheduledArrival = scheduledArrival; }

        public String getRevisedDeparture() { return revisedDeparture; }
        public void setRevisedDeparture(String revisedDeparture) { this.revisedDeparture = revisedDeparture; }

        public String getEstimatedArrival() { return estimatedArrival; }
        public void setEstimatedArrival(String estimatedArrival) { this.estimatedArrival = estimatedArrival; }

        public int getDelayMinutes() { return delayMinutes; }
        public void setDelayMinutes(int delayMinutes) { this.delayMinutes = delayMinutes; }

        public String getGate() { return gate; }
        public void setGate(String gate) { this.gate = gate; }

        public String getTerminal() { return terminal; }
        public void setTerminal(String terminal) { this.terminal = terminal; }

        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    }

    /** Notification card shown in the bell dropdown */
    public static class NotificationResponse {
        private String id;
        private String flightNumber;
        private String type;
        private String title;
        private String message;
        private boolean read;
        private LocalDateTime createdAt;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    /** Compact dashboard row for a tracked flight */
    public static class TrackedFlightSummary {
        private String flightId;
        private String flightNumber;
        private String from;
        private String to;
        private FlightStatus status;
        private String statusLabel;
        private String estimatedArrival;
        private int delayMinutes;

        public String getFlightId() { return flightId; }
        public void setFlightId(String flightId) { this.flightId = flightId; }

        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }

        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }

        public FlightStatus getStatus() { return status; }
        public void setStatus(FlightStatus status) { this.status = status; }

        public String getStatusLabel() { return statusLabel; }
        public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }

        public String getEstimatedArrival() { return estimatedArrival; }
        public void setEstimatedArrival(String estimatedArrival) { this.estimatedArrival = estimatedArrival; }

        public int getDelayMinutes() { return delayMinutes; }
        public void setDelayMinutes(int delayMinutes) { this.delayMinutes = delayMinutes; }
    }

    /** Full dashboard payload: list of tracked flights + unread count */
    public static class FlightDashboardResponse {
        private List<TrackedFlightSummary> trackedFlights;
        private long unreadNotificationCount;

        public List<TrackedFlightSummary> getTrackedFlights() { return trackedFlights; }
        public void setTrackedFlights(List<TrackedFlightSummary> trackedFlights) {
            this.trackedFlights = trackedFlights;
        }

        public long getUnreadNotificationCount() { return unreadNotificationCount; }
        public void setUnreadNotificationCount(long unreadNotificationCount) {
            this.unreadNotificationCount = unreadNotificationCount;
        }
    }

    // ── Inbound ───────────────────────────────────────────────────────────

    /** Body for POST /flights/{id}/simulate — lets testers trigger a scenario */
    public static class SimulateStatusRequest {
        private FlightStatus status;
        private String delayReason;
        private int delayMinutes;
        private String gate;
        private String terminal;

        public FlightStatus getStatus() { return status; }
        public void setStatus(FlightStatus status) { this.status = status; }

        public String getDelayReason() { return delayReason; }
        public void setDelayReason(String delayReason) { this.delayReason = delayReason; }

        public int getDelayMinutes() { return delayMinutes; }
        public void setDelayMinutes(int delayMinutes) { this.delayMinutes = delayMinutes; }

        public String getGate() { return gate; }
        public void setGate(String gate) { this.gate = gate; }

        public String getTerminal() { return terminal; }
        public void setTerminal(String terminal) { this.terminal = terminal; }
    }
}
