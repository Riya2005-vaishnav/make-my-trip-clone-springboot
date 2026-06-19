package com.makemytrip.makemytrip.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * An in-app push notification sent to a user when a tracked flight's
 * status changes (delay, boarding, gate change, etc.).
 */
@Document(collection = "notifications")
public class Notification {

    public enum NotificationType {
        DELAY, ON_TIME, BOARDING, CANCELLED, GATE_CHANGE, ARRIVAL_UPDATE, LANDED
    }

    @Id
    private String id;

    private String userId;

    private String flightId;
    private String flightNumber;

    private NotificationType type;

    /** Short title shown in the notification badge */
    private String title;

    /** Full notification body with context */
    private String message;

    /** Whether the user has seen this notification */
    private boolean read = false;

    private LocalDateTime createdAt;

    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
