package com.makemytrip.makemytrip.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;

@Document(collection = "users")
public class Users {
    @Id
    private String _id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String phoneNumber;
    private List<Booking> bookings = new ArrayList<>();;


    public String getFirstName() {return firstName;}
    public String getId() {
        return _id;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getPassword() {return password;}
    public String getEmail() {return email;}
    public String getRole() {return role;}
    public void setPassword(String password) {this.password = password;}
    public void setRole(String role) {this.role = role;}
    public List<Booking> getBookings(){return bookings;}
    public void setBookings(List<Booking> bookings){this.bookings=bookings;}


    public static class Booking{
        private String type;
        private String bookingId;
        private String resourceId;
        private String date;
        private int quantity;
        private double totalPrice;
        private boolean cancelled = false;
        private String cancellationReason;
        private String refundStatus;
        private double refundAmount;
        private String refundPolicyApplied;
        private String expectedRefundCompletionAt;
        private List<String> selectedSeats = new ArrayList<>();
        private List<String> selectedRooms = new ArrayList<>();

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBookingId() {
            return bookingId;
        }

        public void setBookingId(String bookingId) {
            this.bookingId = bookingId;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public String getCancellationReason() {
            return cancellationReason;
        }

        public void setCancellationReason(String cancellationReason) {
            this.cancellationReason = cancellationReason;
        }

        public String getRefundStatus() {
            return refundStatus;
        }

        public void setRefundStatus(String refundStatus) {
            this.refundStatus = refundStatus;
        }

        public double getRefundAmount() {
            return refundAmount;
        }

        public void setRefundAmount(double refundAmount) {
            this.refundAmount = refundAmount;
        }

        public String getRefundPolicyApplied() {
            return refundPolicyApplied;
        }

        public void setRefundPolicyApplied(String refundPolicyApplied) {
            this.refundPolicyApplied = refundPolicyApplied;
        }

        public String getExpectedRefundCompletionAt() {
            return expectedRefundCompletionAt;
        }

        public void setExpectedRefundCompletionAt(String expectedRefundCompletionAt) {
            this.expectedRefundCompletionAt = expectedRefundCompletionAt;
        }

        public List<String> getSelectedSeats() {
            return selectedSeats;
        }

        public void setSelectedSeats(List<String> selectedSeats) {
            this.selectedSeats = selectedSeats;
        }

        public List<String> getSelectedRooms() {
            return selectedRooms;
        }

        public void setSelectedRooms(List<String> selectedRooms) {
            this.selectedRooms = selectedRooms;
        }
    }
}