package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.dto.FlightStatusDTOs.NotificationResponse;
import com.makemytrip.makemytrip.models.FlightStatus;
import com.makemytrip.makemytrip.models.FlightStatusUpdate;
import com.makemytrip.makemytrip.models.Notification;
import com.makemytrip.makemytrip.models.Notification.NotificationType;
import com.makemytrip.makemytrip.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates, delivers, and marks in-app push notifications.
 *
 * In a production system you would fan these out over Firebase Cloud Messaging
 * or WebSocket SseEmitter; here the same Notification documents are polled
 * via REST so the frontend can implement a "bell" dropdown with live unread counts.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // ── Public API ────────────────────────────────────────────────────────

    /**
     * Derives the right NotificationType and message from a status update,
     * then persists a Notification for every affected user.
     */
    public void sendFlightStatusNotification(String userId,
                                             FlightStatusUpdate update,
                                             FlightStatus previousStatus) {
        Notification notification = buildNotification(userId, update, previousStatus);
        notificationRepository.save(notification);
    }

    /** Returns all notifications for a user, newest first. */
    public List<NotificationResponse> getNotificationsForUser(String userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** Returns only unread notifications for a user. */
    public List<NotificationResponse> getUnreadNotificationsForUser(String userId) {
        return notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** Number of unread notifications — used for the badge count. */
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    /** Marks a single notification as read. */
    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    /** Marks every notification for a user as read at once. */
    public void markAllAsRead(String userId) {
        List<Notification> unread =
                notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private Notification buildNotification(String userId,
                                           FlightStatusUpdate update,
                                           FlightStatus previousStatus) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setFlightId(update.getFlightId());
        n.setFlightNumber(update.getFlightNumber());

        NotificationType type = resolveType(update.getStatus());
        n.setType(type);
        n.setTitle(buildTitle(type, update));
        n.setMessage(buildMessage(update, previousStatus));
        return n;
    }

    private NotificationType resolveType(FlightStatus status) {
        return switch (status) {
            case DELAYED    -> NotificationType.DELAY;
            case BOARDING   -> NotificationType.BOARDING;
            case CANCELLED  -> NotificationType.CANCELLED;
            case LANDED     -> NotificationType.LANDED;
            default         -> NotificationType.ON_TIME;
        };
    }

    private String buildTitle(NotificationType type, FlightStatusUpdate update) {
        return switch (type) {
            case DELAY    -> "✈ Flight " + update.getFlightNumber() + " Delayed";
            case BOARDING -> "🟢 Boarding Now: " + update.getFlightNumber();
            case CANCELLED-> "❌ Flight " + update.getFlightNumber() + " Cancelled";
            case LANDED   -> "🛬 Flight " + update.getFlightNumber() + " Landed";
            default       -> "✅ Flight " + update.getFlightNumber() + " On Time";
        };
    }

    private String buildMessage(FlightStatusUpdate update, FlightStatus previousStatus) {
        StringBuilder sb = new StringBuilder();
        sb.append("Flight ").append(update.getFlightNumber())
          .append(" (").append(update.getFlightId()).append("): ");

        switch (update.getStatus()) {
            case DELAYED -> {
                sb.append("Delayed by ").append(update.getDelayMinutes()).append(" min. ");
                if (update.getDelayReason() != null && !update.getDelayReason().isBlank())
                    sb.append("Reason: ").append(update.getDelayReason()).append(". ");
                if (update.getRevisedDeparture() != null)
                    sb.append("New departure: ").append(update.getRevisedDeparture()).append(". ");
                if (update.getEstimatedArrival() != null)
                    sb.append("Estimated arrival: ").append(update.getEstimatedArrival()).append(".");
            }
            case BOARDING -> {
                sb.append("Now boarding at Gate ").append(update.getGate())
                  .append(", Terminal ").append(update.getTerminal()).append(".");
            }
            case CANCELLED -> {
                sb.append("Has been cancelled. ");
                if (update.getDelayReason() != null)
                    sb.append("Reason: ").append(update.getDelayReason()).append(".");
            }
            case LANDED -> {
                sb.append("Has landed. Actual arrival: ").append(update.getEstimatedArrival()).append(".");
            }
            default -> {
                sb.append("Is on time. Scheduled departure: ")
                  .append(update.getScheduledDeparture()).append(".");
            }
        }
        return sb.toString();
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.setId(n.getId());
        r.setFlightNumber(n.getFlightNumber());
        r.setType(n.getType().name());
        r.setTitle(n.getTitle());
        r.setMessage(n.getMessage());
        r.setRead(n.isRead());
        r.setCreatedAt(n.getCreatedAt());
        return r;
    }
}
