package com.makemytrip.makemytrip.models;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "refunds")
public class Refund {
    @Id
    private String id;
    private String bookingId;
    private String userId;
    private String userEmail;
    private double refundAmount;
    private RefundStatus refundStatus;
    private String cancellationReason;
    private String refundPolicyApplied;
    private LocalDateTime cancelledAt;
    private LocalDateTime expectedCompletionAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public RefundStatus getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(RefundStatus refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getRefundPolicyApplied() {
        return refundPolicyApplied;
    }

    public void setRefundPolicyApplied(String refundPolicyApplied) {
        this.refundPolicyApplied = refundPolicyApplied;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public LocalDateTime getExpectedCompletionAt() {
        return expectedCompletionAt;
    }

    public void setExpectedCompletionAt(LocalDateTime expectedCompletionAt) {
        this.expectedCompletionAt = expectedCompletionAt;
    }
}
