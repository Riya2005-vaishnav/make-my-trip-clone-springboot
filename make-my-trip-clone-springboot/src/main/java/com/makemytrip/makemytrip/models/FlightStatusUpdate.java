package com.makemytrip.makemytrip.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Stores a single status snapshot for a flight, including rich context
 * (delay reason, revised times) so users always know what changed and why.
 */
@Document(collection = "flight_status_updates")
public class FlightStatusUpdate {

    @Id
    private String id;

    /** The flight this update belongs to */
    private String flightId;

    private String flightNumber;

    private FlightStatus status;

    /** Human-readable reason, e.g. "Air traffic congestion at DEL" */
    private String delayReason;

    /** Original scheduled departure */
    private String scheduledDeparture;

    /** Original scheduled arrival */
    private String scheduledArrival;

    /** Revised departure time (same as scheduled when ON_TIME) */
    private String revisedDeparture;

    /** Dynamic estimated arrival (updated in real-time) */
    private String estimatedArrival;

    /** Delay in minutes; 0 when ON_TIME */
    private int delayMinutes;

    private String gate;

    private String terminal;

    /** When this update was recorded */
    private LocalDateTime updatedAt;

    public FlightStatusUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public FlightStatus getStatus() { return status; }
    public void setStatus(FlightStatus status) { this.status = status; }

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

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
