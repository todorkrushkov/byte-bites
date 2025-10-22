package com.example.ByteBites.models.DTO;

public class RestaurantRevenueDTO {
    private String restaurantName;
    private Double totalRevenue;

    public RestaurantRevenueDTO(String restaurantName, Double totalRevenue) {
        this.restaurantName = restaurantName;
        this.totalRevenue = totalRevenue;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}