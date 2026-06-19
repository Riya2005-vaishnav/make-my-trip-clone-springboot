package com.makemytrip.makemytrip.dto;

import java.time.LocalDateTime;

public class DynamicPriceResponse {
    private String itemType;
    private String itemId;
    private double basePrice;
    private double dynamicPrice;
    private double demandMultiplier;
    private double seasonalMultiplier;
    private double totalMultiplier;
    private String reason;
    private LocalDateTime calculatedAt;

    public DynamicPriceResponse(String itemType, String itemId, double basePrice, double dynamicPrice,
                                double demandMultiplier, double seasonalMultiplier, String reason,
                                LocalDateTime calculatedAt) {
        this.itemType = itemType;
        this.itemId = itemId;
        this.basePrice = basePrice;
        this.dynamicPrice = dynamicPrice;
        this.demandMultiplier = demandMultiplier;
        this.seasonalMultiplier = seasonalMultiplier;
        this.totalMultiplier = demandMultiplier * seasonalMultiplier;
        this.reason = reason;
        this.calculatedAt = calculatedAt;
    }

    public String getItemType() {
        return itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getDynamicPrice() {
        return dynamicPrice;
    }

    public double getDemandMultiplier() {
        return demandMultiplier;
    }

    public double getSeasonalMultiplier() {
        return seasonalMultiplier;
    }

    public double getTotalMultiplier() {
        return totalMultiplier;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }
}
