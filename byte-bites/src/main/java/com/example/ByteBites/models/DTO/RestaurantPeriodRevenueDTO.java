package com.example.ByteBites.models.DTO;

import java.math.BigDecimal;

public class RestaurantPeriodRevenueDTO {
    private Long restaurantId;
    private String restaurantName;
    private BigDecimal totalRevenue;

    public RestaurantPeriodRevenueDTO(Long restaurantId, String restaurantName, BigDecimal totalRevenue) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.totalRevenue = totalRevenue;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}