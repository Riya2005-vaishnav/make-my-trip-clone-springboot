package com.makemytrip.makemytrip.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "price_freezes")
public class PriceFreeze {
    @Id
    private String id;
    private String userId;
    private String itemType;
    private String itemId;
    private int quantity;
    private double lockedUnitPrice;
    private double lockedTotalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean used;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLockedUnitPrice() {
        return lockedUnitPrice;
    }

    public void setLockedUnitPrice(double lockedUnitPrice) {
        this.lockedUnitPrice = lockedUnitPrice;
    }

    public double getLockedTotalPrice() {
        return lockedTotalPrice;
    }

    public void setLockedTotalPrice(double lockedTotalPrice) {
        this.lockedTotalPrice = lockedTotalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isActive() {
        return !used && expiresAt != null && expiresAt.isAfter(LocalDateTime.now());
    }
}
