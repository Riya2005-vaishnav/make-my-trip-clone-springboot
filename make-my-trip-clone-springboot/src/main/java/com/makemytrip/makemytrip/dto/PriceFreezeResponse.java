package com.makemytrip.makemytrip.dto;

import java.time.LocalDateTime;

public class PriceFreezeResponse {
    private String freezeId;
    private String itemType;
    private String itemId;
    private int quantity;
    private double lockedUnitPrice;
    private double lockedTotalPrice;
    private LocalDateTime expiresAt;
    private boolean active;

    public PriceFreezeResponse(String freezeId, String itemType, String itemId, int quantity,
                               double lockedUnitPrice, double lockedTotalPrice,
                               LocalDateTime expiresAt, boolean active) {
        this.freezeId = freezeId;
        this.itemType = itemType;
        this.itemId = itemId;
        this.quantity = quantity;
        this.lockedUnitPrice = lockedUnitPrice;
        this.lockedTotalPrice = lockedTotalPrice;
        this.expiresAt = expiresAt;
        this.active = active;
    }

    public String getFreezeId() {
        return freezeId;
    }

    public String getItemType() {
        return itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLockedUnitPrice() {
        return lockedUnitPrice;
    }

    public double getLockedTotalPrice() {
        return lockedTotalPrice;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isActive() {
        return active;
    }
}
