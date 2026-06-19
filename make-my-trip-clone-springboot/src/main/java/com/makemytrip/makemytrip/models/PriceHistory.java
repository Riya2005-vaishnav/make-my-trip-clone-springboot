package com.makemytrip.makemytrip.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "price_history")
public class PriceHistory {
    @Id
    private String id;
    private String itemType;
    private String itemId;
    private double basePrice;
    private double dynamicPrice;
    private double demandMultiplier;
    private double seasonalMultiplier;
    private String reason;
    private LocalDateTime recordedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getDynamicPrice() {
        return dynamicPrice;
    }

    public void setDynamicPrice(double dynamicPrice) {
        this.dynamicPrice = dynamicPrice;
    }

    public double getDemandMultiplier() {
        return demandMultiplier;
    }

    public void setDemandMultiplier(double demandMultiplier) {
        this.demandMultiplier = demandMultiplier;
    }

    public double getSeasonalMultiplier() {
        return seasonalMultiplier;
    }

    public void setSeasonalMultiplier(double seasonalMultiplier) {
        this.seasonalMultiplier = seasonalMultiplier;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
