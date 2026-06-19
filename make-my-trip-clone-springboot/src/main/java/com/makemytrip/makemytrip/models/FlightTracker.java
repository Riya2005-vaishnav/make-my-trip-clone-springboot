package com.makemytrip.makemytrip.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists the set of flights a specific user is actively tracking.
 * One document per user; the tracked flight IDs form a set.
 */
@Document(collection = "flight_trackers")
public class FlightTracker {

    @Id
    private String id;

    private String userId;

    /** IDs of Flight documents being watched */
    private List<String> trackedFlightIds = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FlightTracker() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<String> getTrackedFlightIds() { return trackedFlightIds; }
    public void setTrackedFlightIds(List<String> trackedFlightIds) {
        this.trackedFlightIds = trackedFlightIds;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
