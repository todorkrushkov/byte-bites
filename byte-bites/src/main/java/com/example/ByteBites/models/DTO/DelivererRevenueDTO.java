package com.example.ByteBites.models.DTO;

import java.math.BigDecimal;

public class DelivererRevenueDTO {
    private Long delivererId;
    private String delivererUsername;
    private BigDecimal totalRevenue;
    private boolean bonusAwarded;
    public DelivererRevenueDTO() {
    }

    public DelivererRevenueDTO(Long delivererId, String delivererUsername, BigDecimal totalRevenue) {
        this.delivererId = delivererId;
        this.delivererUsername = delivererUsername;
        this.totalRevenue = totalRevenue;

    }

    public void setBonusAwarded(boolean bonusAwarded) {
        this.bonusAwarded = bonusAwarded;
    }

    public boolean isBonusAwarded() {
        return bonusAwarded;
    }
    public Long getDelivererId() {
        return delivererId;
    }

    public String getDelivererName() {
        return delivererUsername;
    }

    public BigDecimal getTotalIncome() {
        return totalRevenue;
    }


    public void setDelivererId(Long delivererId) {
        this.delivererId = delivererId;
    }

    public void setDelivererName(String delivererName) {
        this.delivererUsername = delivererName;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalRevenue = totalIncome;
    }
}